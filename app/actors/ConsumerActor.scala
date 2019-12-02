package actors

import akka.Done
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future
import akka.actor.{Actor, Props}
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.stream.{ActorMaterializer, ClosedShape}
import com.typesafe.config.Config
import messages.Kernel
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.TopicPartition

import scala.concurrent.ExecutionContext.Implicits.global

object ConsumerActor{
  def props = Props[ConsumerActor]
}

class ConsumerActor extends Actor{

  val config = context.system.settings.config.getConfig("akka.kafka.consumer")
  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers("10.128.0.2:9092")
//      .withClientId("consumerAkka")
      .withGroupId("console-consumer-1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
      .withProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1")
  val kafkaConsumer = consumerSettings.createKafkaConsumer()
//  kafkaConsumer.subscri:be("topico-relacionado")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def business( value: Array[Byte]): Future[Done] = {// ???
  // #atMostOnce
  // #atMostOnce
    val k = (value.map(_.toChar)).mkString
    sender() ! Kernel(k)
    Future.successful(Done)
  }
  var ker: Kernel = Kernel("")
  type Service[A, B] = A => Future[B]
  val handleMessage: Service[String, String] =
    (message) => {
      val k = (message.map(_.toChar)).mkString
      println(k)
      ker  = Kernel(k)

      Future.successful(message.capitalize)
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
      val kafkaTopic = "topico-replicado"
      val partition = 0
      val subscription = Subscriptions.assignment(new TopicPartition(kafkaTopic, partition))
//      val runnableGraph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
//        import akka.stream.scaladsl.GraphDSL.Implicits._
//
//        val kafkaSource = Consumer.plainSource(consumerSettings, subscription)
//        val printlnSink = Sink.foreach(println)
//        val anoSink = Sink.foreach((a : Source[String, String]) => {println(a.toString())})
//        val mapFromConsumerRecord = Flow[ConsumerRecord[Array[Byte], String]].map(record => record.value())
//
//        kafkaSource ~> mapFromConsumerRecord ~> printlnSink ~> anoSink
//
//        ClosedShape
//      })
//
//      runnableGraph.run()
      Consumer
        .committableSource(consumerSettings, Subscriptions.topics("topico-replicado"))
        .mapAsync(1) { msg =>
          handleMessage( (msg.record.value.map(_.toChar)).mkString)
            .flatMap(response => msg.committableOffset.commitScaladsl())
            .recoverWith { case e => msg.committableOffset.commitScaladsl() }
        }
        .runWith(Sink.ignore)

//      println(S)
        sender() ! ker
    }
    case x: Any => println(x)
  }
}
