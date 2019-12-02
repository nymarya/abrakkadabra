package actors

import actors.HelloActor.SayHello
import akka.actor.{Actor, ActorRef, Props}
import messages.{Kernel, KernelData, Matrices, Matrix, Producted}
import org.apache.spark.SparkContext

object MasterActor {
  def props = Props[MasterActor]

  case class convolute(kernel: String);

}

class MasterActor extends Actor {
  val sparkActor = context.actorOf(ConvolutionActor.props, "spark0-actor")
  val cassandraActor = context.actorOf(DatabaseActor.props, "cassandra-actor")
  val kafkaActor: ActorRef = context.actorOf(ProducerActor.props, "producer-actor")
  val kafkaActor2: ActorRef = context.actorOf(ConsumerActor.props, "consumer-actor")

  def receive = {
    case kernelData: KernelData => {
      println(kernelData.kernel)
      kafkaActor ! kernelData
    }
    case Producted => {
      val top :String = "topico-relacionado"

      kafkaActor2 ! top
    }
    case k: Kernel => {
      val matrix = k.value
      val strings: Array[Array[Int]] = matrix.slice(1, matrix.length-1).split(']')
        .map(a => a.replaceAllLiterally("[", "").replaceAllLiterally("]", "").split(',')
          .map( b => b.replaceAllLiterally(" ", "").mkString).filterNot(x => x == "").map(y =>y.toInt)
        ).toArray
      println(strings.toString())

      cassandraActor ! strings

    }
    case m: Matrices => sparkActor ! m
    case bosta: Any => println("bosta"); println(bosta)
  }

}
