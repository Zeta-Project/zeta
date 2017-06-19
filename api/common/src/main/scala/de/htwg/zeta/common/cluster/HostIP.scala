package de.htwg.zeta.common.cluster

import java.net.InetAddress

import scala.util.Try

import grizzled.slf4j.Logging


object HostIP extends Logging {


  def load(): String = InetAddress.getLocalHost.getHostAddress

  def lookupNodeAddress(value: String) = {
    val node = """(\w*):*(\d*)""".r

    val ret = value match {
      case node(hostname, port) =>
        Try(InetAddress.getByName(hostname).getHostAddress).recover {
          case _ => hostname match {
            case "b1" => load()
            case _ => value
          }
        }.map(host => s"$host:$port").getOrElse(value)
      case _ => value
    }

    info(s"looked up seed: $value. Found: $ret")

    ret
  }

  def lookupNode(hostname: String) = {
    Try(InetAddress.getByName(hostname).getHostAddress).map(host => host).getOrElse(hostname)
  }
}
