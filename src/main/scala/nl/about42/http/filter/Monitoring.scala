package nl.about42.http.filter

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}

object Monitoring {

  def monitorTotal(name: String) = mapResponse(finish(name)) & monitor(name)

  def finish(name: String)(r: HttpResponse): HttpResponse = {
    println(s"Mapresponse $name")
    r
  }

  def monitor(name: String): Directive0 = {
      extractHost.flatMap[Unit] {
        case _ => {
          println(s"In m2 for $name")
          pass
        }
      }
  }
}

