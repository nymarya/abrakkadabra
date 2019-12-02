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
//  val cassandraActor = context.actorOf(HelloActor.props, "spark1-actor")
  val kafkaActor: ActorRef = context.actorOf(ProducerActor.props, "producer-actor")
  val kafkaActor2: ActorRef = context.actorOf(ConsumerActor.props, "consumer-actor")

  def receive = {
    case kernelData: KernelData => {
      println(kernelData.kernel)
      kafkaActor ! kernelData
//      kafkaActor.tell(kernelData.kernel, sender())
    }
    case Producted => {
      val top :String = "topico-relacionado"

      kafkaActor2 ! top
    }
    case k: Kernel => println("recebeu resposta do consumidor")
    case bosta: Any => println("bosta"); println(bosta)
  }

}
