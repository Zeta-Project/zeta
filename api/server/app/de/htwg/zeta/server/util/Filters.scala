package de.htwg.zeta.server.util


import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import akka.stream.Materializer
import play.api.Logger
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.api.mvc.Filter
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter

/**
 * Provides filters.
 */
class Filters @Inject() (csrfFilter: CSRFFilter, securityHeadersFilter: SecurityHeadersFilter, mat: Materializer, ec: ExecutionContext) extends HttpFilters {

  override def filters: Seq[EssentialFilter] = Seq(csrfFilter, securityHeadersFilter, new LoggingFilter()(mat, ec))

}

/** Simple Filter to log all requests.
 *
 * @param mat Materializer
 * @param ec ExecutionContext
 */
class LoggingFilter()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      Logger.info(s"${requestHeader.method} ${requestHeader.uri} (took ${requestTime}ms and returned ${result.header.status})")
      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }

}
