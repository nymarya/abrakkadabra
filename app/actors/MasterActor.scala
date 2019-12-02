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
      sender() ! kafkaActor.tell(kernelData, sender())
//      kafkaActor.tell(kernelData.kernel, sender())
    }
    case Producted => sender() ! kafkaActor2.tell("topico-relacionado", sender())
    case Kernel => println("recebeu resposta do consumidor")
  }

}
