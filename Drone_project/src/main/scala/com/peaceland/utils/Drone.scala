package com.peaceland.utils
import io.alphash.faker.{Geolocation, Lorem}
import scala.util.Random

case class Drone(id: Int, latitude: Double, longitude: Double, citizens: List[Citizen], words: String)

object Drone {
  val maxCitizens = 10

  //
  def createDrones(nDrones: Int): List[Drone] ={

    val drones = Range(1, nDrones).map(droneId => {
      val geolocation = Geolocation()
      val citizens = Citizen.createCitizens(Random.nextInt(maxCitizens))
      val words = Lorem().paragraph
      Drone(
        id = droneId,
        latitude = geolocation.latitute,
        longitude = geolocation.longitude,
        citizens = citizens,
        words = words
      )
    })
    drones.toList
  }


}
