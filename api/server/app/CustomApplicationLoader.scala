
import java.io.File
import javax.inject.Singleton

import scala.collection.convert.WrapAsScala

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import com.typesafe.config.ConfigParseOptions
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.common.cluster.HostIP
import de.htwg.zeta.server.controller.generatorControlForwader.ClusterAddressSettings
import grizzled.slf4j.Logging
import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceApplicationLoader
import play.api.inject.guice.GuiceableModule

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

  /**
   * Initiate configuration for builder
   *
   * @param context Application Context instance
   * @return Instance of a builder for an guice application
   */
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val classLoader: ClassLoader = context.environment.classLoader

    val parsed = parseConf("development", classLoader)

    val parsedWithInit = parsed.withFallback(context.initialConfiguration.underlying)
    val nettyConfig = loadConfig(parsedWithInit.resolve())
    val mergedConfig = nettyConfig.withFallback(parsedWithInit).resolve()

    val seeds: List[String] = Option(mergedConfig.getStringList("zeta.actor.cluster")) match {
      case None => Nil
      case Some(javaList) => WrapAsScala.iterableAsScalaIterable(javaList).toList
    }
    val settings = ClusterAddressSettings(seeds.map(HostIP.lookupNodeAddress))

    val clusterAddressBinding: GuiceableModule =
      GuiceableModule.fromPlayBinding(bind[ClusterAddressSettings].to(settings).in[Singleton])
    val modules: List[GuiceableModule] = clusterAddressBinding :: overrides(context).toList

    initialBuilder
      .in(context.environment)
      .loadConfig(Configuration(mergedConfig))
      .overrides(modules: _*)
  }

  /**
   * @param initialConfig the initialConfig
   * @return Config
   */
  private def loadConfig(initialConfig: Config): Config = {

    val clusterConfig = ClusterManager.getLocalNettyConfig(0)

    clusterConfig
  }
}
