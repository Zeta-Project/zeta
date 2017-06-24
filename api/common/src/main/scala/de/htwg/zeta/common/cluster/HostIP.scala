package de.htwg.zeta.common.cluster

import java.net.InetAddress

import scala.util.Try


object HostIP {

  def load(): String = Option(InetAddress.getLocalHost.getHostAddress).get // throw exception when null

  def lookupNodeAddress(value: String): String = {
    value match {
      case IpAddress(address) => address
      case _ => throw new IllegalArgumentException(s"cannot lookup node address: $value. address must have format: ip:port")
    }
  }

  object IpAddress {
    private val delimiter = ":"
    private val maxPortNumber = 65535

    private def parseAddress(addressName: String): Option[String] = {
      val address =
        if (addressName == "localhost") {
          HostIP.load()
        } else {
          InetAddress.getByName(addressName).getHostAddress
        }
      Some(address)
    }

    private def parsePort(portString: String): Option[Int] = {
      val port = portString.toInt
      if (port < 0 || port > maxPortNumber) {
        None
      }
      else {
        Some(port)
      }
    }

    def unapply(ip: String): Option[String] = try {
      val s = ip.split(delimiter, -1).toList
      s.reverse match {
        // list size must be at least 2. tail can be Nil
        case stringPort :: head :: tail =>
          parsePort(stringPort).flatMap(port => parseAddress((head :: tail).reverse.mkString(delimiter)).map(address => s"$address$delimiter$port"))
        case _ => None
      }
    } catch {
      case _: Throwable => None
    }
  }


  def lookupNode(hostname: String) = {
    Try(InetAddress.getByName(hostname).getHostAddress).map(host => host).getOrElse(hostname)
  }
}



