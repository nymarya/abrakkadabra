package actors

import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future
import akka.Done
import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import messages.KernelData

object ProducerActor{
  def props = Props[ProducerActor]
}

class ProducerActor extends Actor{

  val config = context.system.settings.config.getConfig("akka.kafka.producer")
  val producerSettings =
    ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers("10.128.0.2:9092")
  val kafkaProducer = producerSettings.createKafkaProducer()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  def receive = {
    case kernel:  KernelData => {
      println("prod")

      val done = Source(1 to 1)
        .map(_.toString)
        .map(value => new ProducerRecord[String, String]("topico-replicado",kernel.kernel))
        .runWith(Producer.plainSink(producerSettings, kafkaProducer))

      println(done)

    }
  }
}
