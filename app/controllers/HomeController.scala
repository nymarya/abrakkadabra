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
import messages.KernelData
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, system: ActorSystem) (implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  import play.api.data.Form
  import play.api.data.Forms._
  val kernelForm: Form[KernelData] = Form(
    // Defines a mapping that will handle Contact values
    mapping(
      "kernel" -> text
    )(KernelData.apply)(KernelData.unapply)
  )

  def principal = Action { implicit request =>

    val filledKernel = KernelData(kernel = "[ [-1 , -1 , -1], [-1, 8, -1], [-1, -1 , -1]")
    Ok(views.html.actor(kernelForm.fill(filledKernel)))
  }

  val helloActor = system.actorOf(HelloActor.props, "hello-actor")

  implicit val timeout: Timeout = 30.seconds
  def sayHello(name: String) = Action.async {

    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(message)
    }
  }

  def receive = Action { implicit request =>
    kernelForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.actor(formWithErrors))
      },
      contact => {
//        val contactId = Contact.save(contact)
        Redirect("/")
      }
    )
  }

  val sparkActor = system.actorOf(ConvolutionActor.props, "spark-actor")
  def sparkHello() = Action.async {

    (sparkActor ? convolute("aa")).mapTo[String].map { message =>
      Ok(message)
    }
  }

}