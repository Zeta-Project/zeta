package de.htwg.zeta.common.cluster

import java.net.InetAddress
import java.net.UnknownHostException

import grizzled.slf4j.Logging


object HostIP extends Logging {

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def load(): String = Option(InetAddress.getLocalHost.getHostAddress).get // throw exception when null

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def lookupNodeAddress(address: String): String = {
    val ret = address match {
      case AddressParser(addr, port) =>
        IpPort.parseIpPort(addr, port) match {
          case Some(parsedAddress) => parsedAddress
          case None => throw new IllegalArgumentException(s"cannot lookup node address: $address. Could not resolve Host: ${addr}")
        }

      case _ =>
        val msg = s"cannot lookup node address: $address. address must have format: ip:port. port must be in range 0 to ${AddressParser.maxPortNumber}"
        throw new IllegalArgumentException(msg)
    }
    info(s"looked up seed: $address. Found: $ret")
    ret
  }

  def lookupNodeAddressOption(address: String): Option[String] = {
    AddressParser.unapply(address).flatMap(address => IpPort.unapply(address))
  }


  sealed trait AddressParser {
    protected val delimiter = ":"
    protected[HostIP] val maxPortNumber = 65535

    protected def parseAddress(addressName: String): Option[String] = {
      val address =
        if (addressName == "localhost") {
          HostIP.load()
        } else {
          InetAddress.getByName(addressName).getHostAddress
        }
      Option(address)
    }

    protected def parsePort(portString: String): Option[Int] = {
      val port = portString.toInt
      if (port < 0 || port > maxPortNumber) {
        None
      }
      else {
        Some(port)
      }
    }
  }

  object AddressParser extends AddressParser {
    def unapply(ip: String): Option[(String, Int)] = {
      // split with -1 so that trailing spaces won't be discarded. For example invalid:address: will produce ["invalid", "address", ""]
      val s = ip.split(delimiter, -1).toList
      s.reverse match {
        // list size must be at least 2. tail can be Nil
        case stringPort :: head :: tail =>
          parsePort(stringPort).map(port => ((head :: tail).reverse.mkString(delimiter), port))
      }
    }
  }

  object IpPort extends AddressParser {
    def unapply(pair: (String, Int)): Option[String] = try {
      pair match {
        case (ip, port) if port >= 0 && port <= maxPortNumber => parseIpPort(ip, port)
        case _ => None
      }
    } catch {
      case _: UnknownHostException => None
      case _: NoSuchElementException => None
    }

    def parseIpPort(address: String, port: Int): Option[String] = {
      parseAddress(address).map(ip => s"$ip$delimiter$port")
    }

  }

}
