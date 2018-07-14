package nl.about42.streaming

import java.io.File
import java.nio.file.Path

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source, StreamConverters}
import akka.util.ByteString

import scala.io.StdIn

/**
  * Simple application that reads stdin as a bytestream, and then processes the input in various ways
  */
object Streamer extends App {
  implicit val system = ActorSystem("ExampleSystem")

  implicit val mat = ActorMaterializer() // created from `system`

  val s2 = StreamConverters.fromInputStream(() => System.in)

  // flow that looks for the word 'quit' and then completes. Passes input through in all other cases
  val quitWatcher: Flow[ByteString, ByteString, NotUsed] = Flow[ByteString].takeWhile( w => w.decodeString("UTF-8") match {
    case "quit\n" => {println("detected quit"); false}
    case x => {println(s"detected some input: $x"); true}
  })

  val combined = s2.via(quitWatcher)

  val stdoutReporter = Sink.foreach({ w: ByteString => println(s"stdin: ${w.decodeString("UTF-8")}")})

  //val finish = s2.runWith(stdoutReporter)
  val finish = combined.runWith(stdoutReporter)

  implicit val executionContext = system.dispatcher

  finish.onComplete( _ => {
    system.terminate()
  })

}
