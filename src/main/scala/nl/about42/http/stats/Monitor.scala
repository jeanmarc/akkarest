package nl.about42.http.stats

import akka.actor.Actor
import nl.about42.http.stats.Monitor._


object Monitor {
  trait Command
  case class AddStat( key: Key, data: Data) extends Command
  case class Report( name: String) extends Command

  sealed trait Result
  case object SUCCESS extends Result
  case object FAILURE extends Result
  case object REJECTION extends Result


  case class Data( count: Long, durationInNanos: Long)

  case class Key( name: String, request: String, result: Result)


}

class Monitor extends Actor {

  private def myState(myMap: Map[Key, Data]): Receive = {
    case AddStat(k, d) =>
      val oldVal = myMap.getOrElse(k, Data(0, 0))
      println( s"received stat ${k} - ${d} (oldval = ${oldVal}")
      context.become(myState(myMap.updated(k, Data(oldVal.count + d.count, oldVal.durationInNanos + d.durationInNanos))))

    case Report(name) =>
      println(myMap)
  }

  def receive = myState(Map.empty)
}