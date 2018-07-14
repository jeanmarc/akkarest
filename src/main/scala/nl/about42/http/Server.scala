package nl.about42.http

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import nl.about42.http.directive.Monitoring._
import nl.about42.http.stats.Monitor

import scala.io.StdIn

object Server {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("myHttpServer")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val hostname = "localhost"
    val port = 8080

    val monitorActor = system.actorOf(Props[Monitor], "monitoringActor")

    val route: Route = path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hi there</h1"))
      }
    } ~ path("world") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello world</h1"))
      }
    }

    val monitoredRoute = monitoredWithActor("test", monitorActor) {
      route
    }

    val bindingFuture = Http().bindAndHandle(monitoredRoute, hostname, port)

    println(s"Server started on $hostname:$port\nPress RETURN to stop...")

    StdIn.readLine()

    bindingFuture.flatMap(_.unbind()).onComplete(_ => {
      println("Stopping actor system...")
      system.terminate
    })
  }

}