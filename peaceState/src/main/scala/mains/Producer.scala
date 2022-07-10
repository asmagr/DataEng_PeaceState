package mains

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import components.{Drone, Report}
import play.api.libs.json.Json
import java.util.Properties
import java.util.Timer
import java.util.TimerTask

object Producer {
  val nDrones = 10
    val props:Properties = new Properties()
    props.put("bootstrap.servers","localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks","all")
    //props.put("max.block.ms", 30000)

    val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](props)
    val topic = "peace"


  def main(args: Array[String]): Unit ={
    println("Starting producer")
    sendDroneData()

    val timer = new Timer()
    val timerTask = new TimerTask {
      override def run(): Unit = {
        sendDroneData()
      }
    }
    timer.schedule(timerTask, 100, 10000)
  }

  def sendDroneData(): Unit ={
    println(s"Sending data to topic '$topic'")
    val drones = Drone.createDrones(nDrones = nDrones)
    drones.foreach(drone => sendDataInKafka(drone))
  }

  def sendDataInKafka(drone: Drone): Unit ={
    implicit val reportImplicitWrites = Json.writes[Report]
    val droneReport = Json.toJson(Report.createReport(drone))
    val record: ProducerRecord[String, String] =
      new ProducerRecord[String, String](topic, drone.id.toString, droneReport.toString)
    val data = producer.send(record)

    printf(s"sent record(key=%s value=%s) " +
      "meta(partition=%d, offset=%d)\n",
      record.key(), record.value(),
      data.get().partition(),
      data.get().offset())

    println("sending data via kafka" + droneReport.toString())
  }

}
