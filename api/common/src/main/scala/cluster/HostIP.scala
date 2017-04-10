package cluster

object HostIP {

  import java.net.InetAddress

  import scala.util.Try

  def load(): String = InetAddress.getLocalHost.getHostAddress

  def lookupNodeAddress(value: String) = {

    val node = """(\w*):*(\d*)""".r

    value match {
      case node(hostname, port) =>
        Try(InetAddress.getByName(hostname).getHostAddress).map(host => s"$host:$port").getOrElse(value)
      case _ => value
    }
  }

  def lookupNode(hostname: String) = {
    Try(InetAddress.getByName(hostname).getHostAddress).map(host => host).getOrElse(hostname)
  }
}
