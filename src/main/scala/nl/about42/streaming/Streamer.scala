package nl.about42.streaming

import java.io.File
import java.nio.file.Path

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Sink, Source, StreamConverters}

import scala.io.StdIn

/**
  * Simple application that reads stdin as a bytestream, and then processes the input in various ways
  */
object Streamer extends App {
  implicit val system = ActorSystem("ExampleSystem")

  implicit val mat = ActorMaterializer() // created from `system`

  var count = 0
  val inputValues = List("Hello", "world")

  val source: Source[String, NotUsed] = Source(inputValues)

  println("start")
  source.runWith(Sink.foreach({ w =>
    count += 1
    println(w)
  }))
  println(s"done (${count})")


  val s2 = StreamConverters.fromInputStream(() => System.in)

  s2.runWith(Sink.foreach({ w => println(s"stdin: ${w}")}))

  //system.terminate()
  println(s"done2 (${count})")
}
