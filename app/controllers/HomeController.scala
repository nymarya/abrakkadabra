package controllers

import akka.actor.ActorSystem
import javax.inject._
import play.api.mvc._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import actors.{ConvolutionActor, HelloActor}
import actors.HelloActor._
import actors.ConvolutionActor._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, system: ActorSystem) (implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val helloActor = system.actorOf(HelloActor.props, "hello-actor")

  implicit val timeout: Timeout = 30.seconds
  def sayHello(name: String) = Action.async {

    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(message)
    }
  }

  val sparkActor = system.actorOf(ConvolutionActor.props, "spark-actor")
  def sparkHello() = Action.async {

    (sparkActor ? convolute("aa")).mapTo[String].map { message =>
      Ok(message)
    }
  }

}