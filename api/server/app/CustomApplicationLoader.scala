import cluster.ClusterManager
import com.typesafe.config.ConfigFactory
import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject.guice._

class CustomApplicationLoader extends GuiceApplicationLoader() {

  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val classLoader = context.environment.classLoader
    val configuration = Configuration(NodeConfigurator.loadConfig(classLoader))

    initialBuilder
      .in(context.environment)
      .loadConfig(context.initialConfiguration ++ configuration)
      .overrides(overrides(context): _*)
  }
}

object NodeConfigurator {

  /**
   * This method given a class loader will return the configuration object for an ActorSystem
   * in a clustered environment
   *
   * @param classLoader the configured classloader of the application
   * @return Config
   */
  def loadConfig(classLoader: ClassLoader) = {
    val config = ConfigFactory.load(classLoader)

    val seeds = List("b1:2551", "b2:2551")
    val roles = List("api")
    val clusterConfig = ClusterManager.getClusterJoinConfig(roles, seeds, 0).withFallback(ConfigFactory.load())

    clusterConfig
      //.withValue("play.akka.actor-system", ConfigValueFactory.fromAnyRef("ClusterSystem"))
      .withFallback(config)
  }
}
