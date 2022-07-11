package com.peaceland.utils

case class Alert (droneId: Int, latitude:Double, longitude: Double, id: Int, score: Int, name: String) {

  // This function is to announce that we have an alert
  def findTroubleMaker(): Unit ={

    println(s" Found a trouble maker by drone:$droneId at location " +
      s"latitude: $latitude and longitude: $longitude trouble caused by citizen $name with id $id")
  }

}
