package com.peaceland

import com.peaceland.utils.{Drone, Report}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import play.api.libs.json.Json
import java.util.Properties
import java.util.Timer
import java.util.TimerTask
import com.peaceland.constants.{PeaceConstants => PC}

object Producer {

  val nDrones = PC.TEN

  val props:Properties = new Properties()
  props.put("bootstrap.servers","localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks","all")   // ???
  props.put("max.block.ms", PC.FIFTY.toString)  // ???

  val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](props)

  def sendDroneData(): Unit ={
    println(s"Sending data to topic '${PC.TOPIC}'") //logger.info or logger.debug()
    val drones = Drone.createDrones(nDrones = nDrones)
    drones.foreach(drone => sendDataInKafka(drone))
  }

  def sendDataInKafka(drone: Drone): Unit ={
    implicit val reportImplicitWrites = Json.writes[Report]
    val droneReport = Json.toJson(Report.createReport(drone))
    val record: ProducerRecord[String, String] =
      new ProducerRecord[String, String](PC.TOPIC, drone.id.toString, droneReport.toString)
    producer.send(record)
    println("sending data via kafka" + droneReport.toString())
  }

  def main(args: Array[String]): Unit ={
    println("Starting producer") //log4j
    sendDroneData()
      val timer = new Timer()
      val timerTask = new TimerTask {
        override def run(): Unit = {
          sendDroneData()
        }
      }
      timer.schedule(timerTask, PC.FIFTY, PC.HUNDRED)

  }



}
