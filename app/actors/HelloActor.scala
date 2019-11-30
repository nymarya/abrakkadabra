package actors

import akka.actor._

object HelloActor {
  def props = Props[HelloActor]

  case class SayHello(name: String)
}

class HelloActor extends Actor {
  import HelloActor._

  def receive = {
    case SayHello(name: String) =>
      println("hello")
      sender() ! "Hello, " + name
    case kernel: String => "Hello, " + kernel
  }
}