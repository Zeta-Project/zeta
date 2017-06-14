package de.htwg.zeta.common.models.remote

import play.api.libs.json.Reads
import play.api.libs.json.Writes
import rx.lang.scala.Observable

/**
 * Interface to call some remote entity
 */
trait Remote {
  /**
   * Start the execution of a another docker container
   * by the id of the generator and subscribe to a stream to which
   * the started generator will send it's results
   *
   * @param generator The id of the generator
   * @param options The input options to pass to the remote call
   * @tparam Input The input type for the parameters
   * @tparam Output The output type of the generator stream
   * @return A observable with the stream to the generator
   */
  def call[Input, Output](generator: String, options: Input)(implicit writes: Writes[Input], reads: Reads[Output]): Observable[Output]

  /**
   * Publish streams some output to the parent
   *
   * @param value The value which to stream
   * @tparam Output The output type of the stream
   */
  def emit[Output](value: Output)(implicit writes: Writes[Output]): Unit
}
