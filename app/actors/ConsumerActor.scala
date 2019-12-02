package actors

import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.{Keep, Sink}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future
import akka.Done
import akka.actor.{Actor, Props}
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.consumer.ConsumerConfig

import scala.collection.immutable

object ConsumerActor{
  def props = Props[ProducerActor]
}

class ConsumerActor extends Actor{

  val config = context.system.settings.config.getConfig("akka.kafka.consumer")
  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers("10.128.0.2:9092")
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  val kafkaConsumer = consumerSettings.createKafkaConsumer()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  def receive = {
    case topic:  String => {
      val control: DrainingControl[immutable.Seq[Done]] =
        Consumer
          .atMostOnceSource(consumerSettings, Subscriptions.topics(topic))
          .mapAsync(1)(record =>{
            sender() ! record.value()

            Future.successful(Done)
          })
          .toMat(Sink.seq)(Keep.both)
          .mapMaterializedValue(DrainingControl.apply)
          .run()

    }
  }
}
