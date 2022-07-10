package mains

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import java.time.Duration
import java.util.{Properties, Timer, TimerTask}
import scala.collection.JavaConverters._
import play.api.libs.json._

object Consumer {
  val props:Properties = new Properties()
  props.put("group.id", "test")
  props.put("bootstrap.servers","localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")

  val topic = "peace"

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)

  def main(args: Array[String]): Unit = {
    println("Starting Consumer")
    println(s"Subscribed to topic $topic")
    consumer.subscribe(List(topic).asJava)

    val timer = new Timer()
    val timerTask = new TimerTask {
      override def run(): Unit = {
        receiveFromKafka()
      }
    }

    timer.schedule(timerTask, 100, 10000)

  }

  def receiveFromKafka(): Unit = {
    val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))
    records.asScala.foreach(record => {
      val rr = record.value()
      val json: JsValue = Json.parse(rr)
      println("received data:", json.result.toString())
    })
  }
}
