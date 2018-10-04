package nl.about42.streaming

import java.io.File
import java.nio.file.Path

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source, StreamConverters}
import akka.util.ByteString

import scala.io.StdIn

/**
  * Simple application that reads stdin as a bytestream, and then processes the input in various ways
  */
object Streamer extends App {
  implicit val system = ActorSystem("ExampleSystem")

  implicit val mat = ActorMaterializer() // created from `system`

  val s2 = StreamConverters.fromInputStream(() => System.in).log("stdin").withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel, onFinish = Logging.InfoLevel))

  // flow that looks for the word 'quit' and then completes. Passes input through in all other cases
  val quitWatcher = Flow[ByteString].takeWhile( w => ! "quit\n".equals(w.utf8String))

  val combined = s2.via(quitWatcher).log("after-quitWatcher").withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel, onFinish = Logging.InfoLevel))

  val stdoutReporter = Sink.foreach({ w: ByteString => println(s"stdout: ${w.utf8String}")})

  //val finish = s2.runWith(stdoutReporter)
  val finish = combined.runWith(stdoutReporter)

  implicit val executionContext = system.dispatcher

  finish.onComplete( _ => {
    println("stopping the application")
    system.terminate()
    println("after terminate")
  })

}
