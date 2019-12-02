package actors

import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.cassandra.scaladsl.CassandraSource
import akka.stream.scaladsl.Sink
import com.datastax.driver.core.{Cluster, SimpleStatement}

object DatabaseActor{
  def props = Props[DatabaseActor]
}

class DatabaseActor extends Actor{

  implicit val session = Cluster.builder
    .addContactPoint("10.128.0.2")
    .withPort(9042)
    .build
    .connect()

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def receive = {
    case rows: Int => {
      println("cass")
      val keyspaceName = "abrakkadabra"
      val stmt = new SimpleStatement(s"SELECT * FROM $keyspaceName.matrixes WHERE row=$rows").setFetchSize(1)
      val results = CassandraSource(stmt).runWith(Sink.seq)
      println(results)
    }
  }

}
