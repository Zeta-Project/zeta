package de.htwg.zeta.common.cluster

import java.net.InetAddress

import scala.util.Try
import scala.util.matching.Regex

import grizzled.slf4j.Logging


object HostIP extends Logging {

  val addressRegex: Regex = """(\w+):(\d+)""".r

  def load(): String = InetAddress.getLocalHost.getHostAddress

  def lookupNodeAddress(value: String): String = {

    val ret = value match {
      case addressRegex(hostname, port) =>
        Try(InetAddress.getByName(hostname).getHostAddress).map(host => s"$host:$port").getOrElse(value)
      case _ => value
    }

    info(s"looked up seed: $value. Found: $ret")

    ret
  }

  def lookupNode(hostname: String): String = {
    Try(InetAddress.getByName(hostname).getHostAddress).map(host => host).getOrElse(hostname)
  }
}
