package actors

import akka.Done
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.stream.scaladsl.{Keep, Sink}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future
import akka.actor.{Actor, Props}
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import messages.Kernel
import org.apache.kafka.clients.consumer.ConsumerConfig

import scala.collection.immutable

object ConsumerActor{
  def props = Props[ConsumerActor]
}

class ConsumerActor extends Actor{

  val config = context.system.settings.config.getConfig("akka.kafka.consumer")
  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers("10.128.0.2:9092")
      .withGroupId("console-consumer-77977")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
      .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")
  val kafkaConsumer = consumerSettings.createKafkaConsumer()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def business( value: Array[Byte]): Future[Done] = {// ???
  // #atMostOnce
  // #atMostOnce
    val k = (value.map(_.toChar)).mkString
    sender() ! Kernel(k)
    Future.successful(Done)
  }


  def receive = {
    case topic:  String => {
      println("cons")
      println(topic)
//      val control: DrainingControl[immutable.Seq[Done]] =
//        Consumer
//          .at(consumerSettings, Subscriptions.topics(topic))
//          .mapAsync(1)(record => business(record.value()))
//          .toMat(Sink.seq)(Keep.both)
//          .mapMaterializedValue(DrainingControl.apply)
//          .run()

      val committerSettings = CommitterSettings(context.system)

      val control: DrainingControl[Done] =
        Consumer
          .committableSource(consumerSettings, Subscriptions.topics(topic))
          .mapAsync(1) { msg =>
            business( msg.record.value)
              .map(_ => msg.committableOffset)
          }
          .toMat(Committer.sink(committerSettings))(Keep.both)
          .mapMaterializedValue(DrainingControl.apply)
          .run()

    }
    case x: Any => println(x)
  }
}
