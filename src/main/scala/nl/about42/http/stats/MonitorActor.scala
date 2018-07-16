package nl.about42.http.stats

import akka.actor.Actor
import nl.about42.http.stats.MonitorActor._


object MonitorActor {
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

class MonitorActor extends Actor {

  private def myState(myMap: Map[Key, Data]): Receive = {
    case AddStat(k, d) =>
      val oldVal = myMap.getOrElse(k, Data(0, 0))
      context.become(myState(myMap.updated(k, Data(oldVal.count + d.count, oldVal.durationInNanos + d.durationInNanos))))

    case Report(name) =>
      //sender() ! myMap.filterKeys(p => p.name == name).toString() + "\n"
      sender() ! report(myMap.filterKeys(p => p.name == name))
  }

  def receive = myState(Map.empty)

  private def report( map: Map[Key, Data]): String = {
    def keyString(key: Key): String = s"[${key.result}] ${key.request}"
    val prefix = """{"stats":["""
    val suffix = """]}"""
    val items = map.map( item => {
      val avg: Long = if (item._2.count == 0) 0 else item._2.durationInNanos / item._2.count
      s"""{"key":"${keyString(item._1)}","count": ${item._2.count},"totalTime":${item._2.durationInNanos},"average":${avg}}"""
    }).mkString(",")
    prefix + items + suffix
  }
}