package de.htwg.zeta.generatorControl.start

import org.rogach.scallop.ScallopConf
import org.rogach.scallop.ScallopOption

/**
 */
class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val devPort: ScallopOption[Int] = opt[Int]()
  val devSeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  val dummyPort: ScallopOption[Int] = opt[Int]()
  val dummySeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  val masterNum: ScallopOption[Int] = opt[Int]()
  val masterPort: ScallopOption[Int] = opt[Int]()
  val masterSeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  val workers: ScallopOption[Int] = opt[Int]()
  val workerSeeds: ScallopOption[List[String]] = opt[List[String]](default = Some(List()))
  verify()
}
