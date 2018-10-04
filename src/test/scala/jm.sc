import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration._

// trying stuff with futures

// Goal: some code to go from the input to the output

// input: Set of Future[Map[String, Int]]
// output: 1 Future[Map[String, Int]], with joined maps + ("total" -> sum)

implicit val ec = ExecutionContext.global

// creating the input
val f1 = Future{Map("a" -> 1)}
val f2 = Future{Map("b" -> 9)}
val f3 = Future{Map("c" -> 42, "C" -> 420)}
val f4 = Future{Map("d" -> 0)}


val input = Set(f1, f2, f3, f4)

val interim = Future.sequence(input).map(_.flatten.toMap)

val output = interim.map(r => {
  val total = r.foldLeft(0)(_ + _._2)
  r + ("total" -> total)
})

Await.ready(output, 2.seconds)

output
