package com.peaceland

import com.peaceland.utils.{Alert}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import com.peaceland.constants.{PeaceConstants => PC}
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
  props.put("auto.commit.interval.ms", "1000")
  props.put("enable.auto.commit", "false")
  props.put("auto.offset.reset", "earliest")

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)

  def main(args: Array[String]): Unit = {
    println("Starting Consumer")
    println(s"Subscribed to topic ${PC.TOPIC}")
    consumer.subscribe(List(PC.TOPIC).asJava)

    val timer = new Timer()
    val timerTask = new TimerTask {
      override def run(): Unit = {
        receiveFromKafka()
      }
    }

    timer.schedule(timerTask, PC.THOUSAND, PC.TEN_THOUSAND)
  }

  def receiveFromKafka(): Unit = {
    val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(PC.HUNDRED))
    records.asScala.foreach(record => {
      val rr = record.value()
      val json = Json.parse(rr)

      val droneId = (json \\ "droneId").toArray.head.as[Int]
      val latitude = (json \\ "latitude").toArray.head.as[Double]
      val longitude = (json \\ "longitude").toArray.head.as[Double]
      val citizensId = (json \\ "citizensId").toArray.head.as[List[Int]]
      val citizensScore = (json \\ "citizensScore").toArray.head.as[List[Int]]
      val citizensName = (json \\ "citizensName").toArray.head.as[List[String]]

      (citizensId, citizensScore, citizensName).zipped.foreach( (id, score, name) => {
        if(score > PC.ALERT_THRESHOLD){
          val alert = Alert(droneId, latitude, longitude, id, score, name)
          alert.findTroubleMaker()
        }
      })

      //println("received data:", droneId, latitude, longitude, citizensId)
    })
  }
}
