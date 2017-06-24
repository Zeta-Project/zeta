
import java.io.File
import javax.inject.Singleton

import scala.collection.convert.WrapAsScala
import scala.sys.process.Process

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import com.typesafe.config.ConfigParseOptions
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.common.cluster.HostIP
import de.htwg.zeta.common.cluster.ClusterAddressSettings
import grizzled.slf4j.Logging
import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceApplicationLoader
import play.api.inject.guice.GuiceableModule
import CustomApplicationLoader.Production
import CustomApplicationLoader.Development

/**
 * Entrypoint of application
 */
class CustomApplicationLoader extends GuiceApplicationLoader() with Logging {


  private def parseConf(baseName: String, classLoader: ClassLoader): Config = {
    val conf = if (baseName.endsWith(".conf")) baseName else baseName + ".conf"

    val configParserOpts = ConfigParseOptions.defaults().setClassLoader(classLoader)
    val file: File = new java.io.File(classLoader.getResource(conf).toURI)
    ConfigFactory.parseFile(file, configParserOpts)
  }

  private def checkEnvironment(config: Configuration): CustomApplicationLoader.Environment = {

    val envOpt =
      config.getString("zeta.deployment.environment").collect {
        case Production(env) => env
        case Development(env) => env
      }

    envOpt match {
      case Some(env) =>
        env

      case None =>
        val msg =
          s"""Please set the Environment Variable "ZETA_DEPLOYMENT" to either "${Development.asString}" or "${Production.asString}"."""
        throw new IllegalArgumentException(msg)
    }

  }


  /**
   * Initiate configuration for builder
   *
   * @param context Application Context instance
   * @return Instance of a builder for an guice application
   */
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val classLoader: ClassLoader = context.environment.classLoader

    val environment = checkEnvironment(context.initialConfiguration)

    val parsed = parseConf(environment.asString, classLoader)

    val parsedWithInit = parsed.withFallback(context.initialConfiguration.underlying)
    val nettyConfig = ClusterManager.getLocalNettyConfig(0)
    val mergedConfig = nettyConfig.withFallback(parsedWithInit).resolve()

    val seeds: List[String] = {
      val list = buildSeeds(mergedConfig)
      environment match {
        case Development => list.take(1)
        case Production => list
      }
    }

    if (seeds.isEmpty) throw new IllegalArgumentException("zeta.actor.cluster must be defined in config.")

    if (environment.isDevelopment) handleDevThread(seeds)

    val settings = ClusterAddressSettings(seeds)

    val clusterAddressBinding: GuiceableModule =
      GuiceableModule.fromPlayBinding(bind[ClusterAddressSettings].to(settings).in[Singleton])
    val modules: List[GuiceableModule] = clusterAddressBinding :: overrides(context).toList

    initialBuilder
      .in(context.environment)
      .loadConfig(Configuration(mergedConfig))
      .overrides(modules: _*)
  }


  private def buildSeeds(mergedConfig: Config): List[String] = {
    Option(mergedConfig.getStringList("zeta.actor.cluster")) match {
      case None => Nil
      case Some(javaList) =>
        val list = WrapAsScala.iterableAsScalaIterable(javaList).toList
        list.map(HostIP.lookupNodeAddress)
    }
  }


  private val clusterGroup = "clusterThreadGroup"

  private def getGroup(): ThreadGroup = {
    val current = Thread.currentThread().getThreadGroup
    val arr: Array[ThreadGroup] = Array.ofDim(100)
    current.enumerate(arr, /* recurse = */ false)

    val list = arr.toList.filterNot(_ == null)

    list.find(_.getName == clusterGroup) match {
      case Some(group) =>
        info("reusing existing thread group")
        val threadArr: Array[Thread] = Array.ofDim(group.activeCount() + 100)
        group.enumerate(threadArr, /* recurse = */ true)
        val threadList = threadArr.toList.filterNot(_ == null)
        threadList.foreach(_.interrupt())
        Thread.sleep(1000)
        threadList.filter(_.isAlive).foreach(_.stop())
        group
      case None =>
        info("creating new thread group")
        new ThreadGroup(clusterGroup)
    }

  }


  private def handleDevThread(seeds: List[String]): Unit = {
    val group = getGroup()


    val commandList = List("sbt",
      "project generatorControl",
      "run --master-port 2551 --master-num 1 --workers 3 --worker-seeds localhost:2551 --dev-port 2552 --dev-seeds localhost:2551")

    val windowsStarter = List("cmd.exe", "/c")


    val cmd: List[String] =
      if (System.getProperty("os.name").startsWith("Windows")) {
        windowsStarter ++ commandList
      } else {
        commandList
      }

    val thread = new Thread(group, new Runnable {
      override def run(): Unit = {
        val p = Process(cmd).run()
        try {
          p.exitValue()
        } catch {
          case _: Throwable =>
            p.destroy()
        }
      }
    })

    thread.start()
  }

}


object CustomApplicationLoader {

  sealed abstract class Environment(val asString: String) {
    def unapply(arg: String): Option[Environment] = if (arg.toLowerCase() == asString) Some(this) else None

    def isDevelopment: Boolean = this == Development

    def isProduction: Boolean = this == Production
  }

  object Development extends Environment("development")

  object Production extends Environment("production")

  val isDevMode: Boolean = true
}
