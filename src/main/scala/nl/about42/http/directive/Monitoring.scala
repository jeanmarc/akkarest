package nl.about42.http.directive

import akka.http.scaladsl.server.{Directive0, RequestContext, RouteResult}
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}

import scala.util.{Failure, Success, Try}

object Monitoring {

  def monitored: Directive0 = WrapperDirectives.aroundRequest(timeRequest)

  private def timeRequest(ctx: RequestContext): Try[RouteResult] => Unit = {
    val start = System.nanoTime()

    {
      case Success(Complete(resp)) =>
        val d = System.nanoTime() - start
        println(s"[${resp.status.intValue()}] ${ctx.request.method.name} " +
          s"${ctx.request.uri} took: ${d}ns")
      case Success(Rejected(_)) =>
        val d = System.nanoTime() - start
        println(s"Rejected ${ctx.request.method.name} " +
          s"${ctx.request.uri} took: ${d}ns")
      case Failure(_)           =>
        val d = System.nanoTime() - start
        println(s"Failed ${ctx.request.method.name} " +
          s"${ctx.request.uri} took: ${d}ns")
    }
  }


}

