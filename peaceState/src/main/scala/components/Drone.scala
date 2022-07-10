package components

import io.alphash.faker.{Geolocation, Lorem}
import scala.util.Random
import scala.collection.mutable.MutableList

case class Drone(id: Int, latitude: Double, longitude: Double, citizens: List[Citizen], words: String)

object Drone {
  def createDrones(nDrones: Int): List[Drone] ={
    val drones: MutableList[Drone] = MutableList()
    for(i <- 1 to nDrones){
      val geolocation = Geolocation()
      val maxCitizens = 10
      val citizens = Citizen.createCitizens(Random.nextInt(maxCitizens))
      val words = Lorem().paragraph
      drones += Drone(
        id = i,
        latitude = geolocation.latitute,
        longitude = geolocation.longitude,
        citizens = citizens,
        words = words
      )
    }
    drones.toList
  }


}
