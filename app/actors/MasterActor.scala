package actors

import actors.HelloActor.SayHello
import akka.actor.{Actor, Props}
import messages.KernelData
import org.apache.spark.SparkContext

object MasterActor {
  def props = Props[MasterActor]

  case class convolute(kernel: String);

}

class MasterActor extends Actor {
  val sparkActor = context.actorOf(HelloActor.props, "spark0-actor")
  val cassandraActor = context.actorOf(HelloActor.props, "spark1-actor")
  val kafkaActor = context.actorOf(HelloActor.props, "spark2-actor")

  def receive = {
    case kernelData: KernelData => {
      println(kernelData.kernel)
      sender() ! kafkaActor.tell(kernelData.kernel, sender())
//      kafkaActor.tell(kernelData.kernel, sender())
    }
//    case KernelData =>

  }

}
