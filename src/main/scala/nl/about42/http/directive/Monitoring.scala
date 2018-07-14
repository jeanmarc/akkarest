package nl.about42.http.directive

import akka.actor.ActorRef
import akka.http.scaladsl.server.{Directive0, RequestContext, RouteResult}
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}
import nl.about42.http.stats.Monitor._

import scala.util.{Failure, Success, Try}

object Monitoring {

  def monitoredWithActor(name: String, actor: ActorRef): Directive0 = WrapperDirectives.aroundRequest(monitorRequest(name, actor))

  private def monitorRequest(name: String, actor: ActorRef)(ctx: RequestContext): Try[RouteResult] => Unit = {
    val start = System.nanoTime()

    collectStats(name, actor, start, ctx)
  }

  private def collectStats(name: String, actor: ActorRef, start: Long, ctx: RequestContext)(result: Try[RouteResult]): Unit = {
    val duration = System.nanoTime() - start
    val request = ctx.request.method.value + " " + ctx.request.uri.toString()
    val status = result match {
      case Success(Complete(resp)) => SUCCESS
      case Success(Rejected(_))    => REJECTION
      case Failure(_)              => FAILURE
    }
    actor ! AddStat(Key(name, request, status), Data(1, duration))

  }

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

