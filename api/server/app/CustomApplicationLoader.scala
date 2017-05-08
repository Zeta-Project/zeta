import cluster.ClusterManager
import com.typesafe.config.ConfigFactory
import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceApplicationLoader
import scala.collection.JavaConversions.iterableAsScalaIterable

/**
 * Entrypoint of application
 */
class CustomApplicationLoader extends GuiceApplicationLoader() {

  /**
   * Initiate configuration for builder
   * @param context Application Context instance
   * @return Instance of a builder for an guice application
   */
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val classLoader = context.environment.classLoader
    val configuration = Configuration(loadConfig(classLoader))

    initialBuilder
      .in(context.environment)
      .loadConfig(context.initialConfiguration ++ configuration)
      .overrides(overrides(context): _*)
  }

  /**
   * This method given a class loader will return the configuration object for an ActorSystem
   * in a clustered environment
   *
   * @param classLoader the configured classloader of the application
   * @return Config
   */
  private def loadConfig(classLoader: ClassLoader) = {
    val config = ConfigFactory.load(classLoader)

    val seeds = config.getStringList("zeta.actor.cluster").toList
    val roles = List("api")
    val clusterConfig = ClusterManager.getClusterJoinConfig(roles, seeds, 0).withFallback(ConfigFactory.load())

    clusterConfig.withFallback(config)
  }
}
