package nl.about42.http.directive

import akka.http.scaladsl.server.{Directive0, RequestContext, RouteResult}
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}

import scala.util.{Failure, Success, Try}

object Monitoring {

  def monitored: Directive0 = WrapperDirectives.aroundRequest(timeRequest)

  private def timeRequest(ctx: RequestContext): Try[RouteResult] => Unit = {
    val start = System.nanoTime()

    handleResult(start, ctx)
  }

  private def handleResult(start: Long, ctx: RequestContext)(result: Try[RouteResult]): Unit = {
    val duration = (System.nanoTime() - start) / 1000000.0

    result match {
      case Success(Complete(resp)) =>
        println(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${duration}ms")
      case Success(Rejected(_)) =>
        println(s"Rjctd ${ctx.request.method.name} ${ctx.request.uri} took: ${duration}ms")
      case Failure(_)           =>
        println(s"Faild ${ctx.request.method.name} ${ctx.request.uri} took: ${duration}ms")

    }

  }


}

