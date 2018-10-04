package nl.about42.http.directive

import akka.actor.ActorRef
import akka.http.scaladsl.server.{Directive0, RequestContext, RouteResult}
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}
import nl.about42.http.stats.MonitorActor._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

object Monitoring {

  def monitoredWithActor(name: String, actor: ActorRef): Directive0 = WrapperDirectives.aroundRequest(monitorRequest(name, actor))

  private def monitorRequest(name: String, actor: ActorRef)(ctx: RequestContext): Try[RouteResult] => Unit = {
    val start = System.nanoTime()

    collectStats(name, actor, start, ctx)
  }

  private def collectStats(name: String, actor: ActorRef, start: Long, ctx: RequestContext)(result: Try[RouteResult]): Unit = {
    println(ctx)
    val duration = System.nanoTime() - start
    val request = ctx.request.method.value + " " + ctx.request.uri.toString()
    val status = result match {
      case Success(Complete(resp)) => SUCCESS
      case Success(Rejected(_))    => REJECTION
      case Failure(_)              => FAILURE
    }
    actor ! AddStat(Key(name, request, status), Data(1, duration))

  }

}

