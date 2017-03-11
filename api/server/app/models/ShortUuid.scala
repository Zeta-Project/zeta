package models

import scala.util.Random

/**
 * Custom unique id implementation using an alphanumeric string
 * => Ids are 13 digits long
 * Collision probability for 1 million Ids is 1-e^((-1.000.000^2)/(2*2^(8*9))) = 1*10^(-10)
 */
object ShortUuid { def uuid = (Random.alphanumeric take 13).mkString }