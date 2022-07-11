package com.peaceland.utils

case class Report(droneId: Int, latitude: Double, longitude: Double, citizensId: Array[Int], citizensScore: Array[Int],
                  citizensName: Array[String], words: String)
object Report {

  // This is to generate a report with all necessary information
  def createReport(drone: Drone): Report ={

    Report(
      droneId = drone.id,
      latitude = drone.latitude,
      longitude = drone.longitude,
      citizensId = drone.citizens.map(citizen => citizen.id).toArray,
      citizensScore = drone.citizens.map(citizen => citizen.score).toArray,
      citizensName = drone.citizens.map(citizen => citizen.name).toArray,
      words = drone.words
    )
  }

}
