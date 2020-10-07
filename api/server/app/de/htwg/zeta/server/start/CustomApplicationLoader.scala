package de.htwg.zeta.server.start

import java.io.File

import com.google.inject.Guice
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.impl.ConfigImpl
import de.htwg.zeta.common.cluster.ClusterAddressSettings
import de.htwg.zeta.common.cluster.ClusterManager
import de.htwg.zeta.common.cluster.HostIP
import de.htwg.zeta.persistence.PersistenceModule
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import grizzled.slf4j.Logging
import javax.inject.Singleton

import scala.collection.JavaConverters

import de.htwg.zeta.server.start.CustomApplicationLoader.DevDeployment
import de.htwg.zeta.server.start.CustomApplicationLoader.ProdDeployment
import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceApplicationLoader
import play.api.inject.guice.GuiceableModule

/**
 * Entrypoint of application. The config is loaded / generated here.
 * Part of the config is parsed and added into the dependency injection.
 * If the config is incorrect there will be an Exception and the application will shut down.
 * <p>
 * It is the [[CustomApplicationLoader]] that decides whether or not the Application will start in production or development mode.
 * Depending on this decision different configuration files will be loaded.
 * In development mode [[de.htwg.zeta.server.start.DevelopmentStarter]] will spawn a child-process that will run [[de.htwg.zeta.generatorControl.Main]]
 *
 */
class CustomApplicationLoader extends GuiceApplicationLoader() with Logging {
  info("CustomApplicationLoader started")

  /**
   * Initiate configuration for builder
   *
   * @param context Application Context instance
   * @return Instance of a builder for an guice application
   */
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val classLoader: ClassLoader = context.environment.classLoader

    val environment = CustomApplicationLoader.checkEnvironment(context.initialConfiguration) match {
      case Some(env) => env
      case None =>
        noDeploymentMessage()
        DevDeployment
    }

    DevelopmentStarter(environment, classLoader)

    val config = mergeConfigs(environment, classLoader, context.initialConfiguration.underlying)

    val seeds: List[String] = environment match {
      case CustomApplicationLoader.DevDeployment => {
        warn(s"Application runs in development mode.")
        List(s"${HostIP.load()}:${CustomApplicationLoader.devPort}")
      }
      case CustomApplicationLoader.ProdDeployment => buildSeeds(config)
    }

    if (seeds.isEmpty) throw new IllegalArgumentException("zeta.actor.cluster must be defined in config.")

    val settings = ClusterAddressSettings(seeds)

    val clusterAddressBinding: GuiceableModule =
      GuiceableModule.fromPlayBinding(bind[ClusterAddressSettings].to(settings).in[Singleton])
    val modules: List[GuiceableModule] = clusterAddressBinding :: overrides(context).toList

    val injector = Guice.createInjector(new PersistenceModule)
    val generatorImageRepo = injector.getInstance(classOf[GeneratorImageRepository])
    GeneratorImageSetup(generatorImageRepo)

    initialBuilder
      .in(context.environment)
      .loadConfig(Configuration(config))
      .overrides(modules: _*)
  }

  private def noDeploymentMessage() =
    warn(s"""Please set the Environment Variable "ZETA_DEPLOYMENT" to either "${
      DevDeployment.asString
    }" or "${
      ProdDeployment.asString
    }".""")

  private def mergeConfigs(environment: CustomApplicationLoader.DeploymentMode, classLoader: ClassLoader, initialConfiguration: Config): Config = {
    val parsed = parseConf(environment.asString, classLoader)

    val parsedWithInit = parsed.withFallback(initialConfiguration)
    val nettyConfig = ClusterManager.getLocalNettyConfig(0)
    ConfigImpl.systemPropertiesAsConfig().withFallback(nettyConfig.withFallback(parsedWithInit)).resolve()
  }

  private def parseConf(baseName: String, classLoader: ClassLoader): Config = {
    val conf = if (baseName.endsWith(".conf")) baseName else baseName + ".conf"

    val configParserOpts = ConfigParseOptions.defaults().setClassLoader(classLoader)
    val file: File = new java.io.File(classLoader.getResource(conf).toURI)
    ConfigFactory.parseFile(file, configParserOpts)
  }


  private def buildSeeds(mergedConfig: Config): List[String] = {
    Option(mergedConfig.getStringList("zeta.actor.cluster")) match {
      case None => Nil
      case Some(javaList) =>
        val list = JavaConverters.iterableAsScalaIterable(javaList).toList
        list.map(HostIP.lookupNodeAddress)
    }
  }

}

object CustomApplicationLoader {
    def checkEnvironment(config: Configuration): Option[CustomApplicationLoader.DeploymentMode] = {
      config.get[String]("zeta.deployment.environment") match {
        case ProdDeployment(env) => Some(env)
        case DevDeployment(env) => Some(env)
        case _ => None
      }
  }


  /**
   *
   * TODO replace with [[play.api.Mode]]
   */
  sealed abstract class DeploymentMode(val asString: String) {
    def unapply(arg: String): Option[DeploymentMode] = if (arg.toLowerCase() == asString) Some(this) else None

    def isDevelopment: Boolean = this == DevDeployment

    def isProduction: Boolean = this == ProdDeployment
  }

  object DevDeployment extends DeploymentMode("development")

  object ProdDeployment extends DeploymentMode("production")

  val devPort: Int = 2551

}
