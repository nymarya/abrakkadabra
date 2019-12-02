package actors

import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.cassandra.scaladsl.CassandraSource
import akka.stream.scaladsl.Sink
import com.datastax.driver.core.{Cluster, SimpleStatement}
import messages.Matrices

import scala.concurrent.{Await, TimeoutException}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseActor{
  def props = Props[DatabaseActor]
}

class DatabaseActor extends Actor{

  implicit val session = Cluster.builder
    .addContactPoint("10.128.0.4")
    .withPort(9042)
    .build
    .connect()

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def receive = {
    case strings: Array[Array[Int]] => {
      println("cassandra")
      val rows = strings(0).length
      val keyspaceName = "abrakkadabra"
      val stmt = new SimpleStatement(s"SELECT * FROM $keyspaceName.matrixes WHERE rows=$rows limit 1 ALLOW FILTERING")
      val results = CassandraSource(stmt).runWith(Sink.seq)

      val d : Duration = 1.seconds
      try{
        Await.ready(results, d)
      } catch{
        case x : TimeoutException => println("")
      }

      var m: Matrices = Matrices(strings, strings )
      results.foreach( item => {
        val matrix :String = item.head.getString(1)
        println(matrix)
        val matrixNew: Array[Array[Int]] = matrix.slice(1, matrix.length-1).split(']')
          .map(a => a.replaceAllLiterally("[", "").replaceAllLiterally("]", "").split(',')
            .map( b => b.replaceAllLiterally(" ", "").mkString).filterNot(x => x == "").map(y =>y.toInt)
          ).toArray
        m = Matrices(strings, matrixNew )


      })

      sender() ! m
    }
  }

}
