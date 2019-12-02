package actors

import actors.HelloActor.SayHello
import akka.actor.{Actor, ActorRef, Props}
import messages.{Kernel, KernelData, Producted}
import org.apache.spark.SparkContext

object MasterActor {
  def props = Props[MasterActor]

  case class convolute(kernel: String);

}

class MasterActor extends Actor {
//  val sparkActor = context.actorOf(HelloActor.props, "spark0-actor")
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
      val strings: Array[Array[String]] = k.value.split(',').map(a => a.split(','))

      cassandraActor ! strings(0).length

    }
    case bosta: Any => println("bosta"); println(bosta)
  }

}
