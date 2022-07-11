package com.peaceland.utils
import io.alphash.faker.Person  // this is a PHP library that generates fake data of all sorts
import scala.util.Random

case class Citizen(id: Int, name: String, score: Int)

object Citizen {

  val maxScore = 100
  val minScore = 0

 // This list is to create n citizens and put them in a list
  def createCitizens(n: Int): List[Citizen] ={
    val citizens = Range(2,n).map(citizenId => {
      val score = Random.nextInt( (maxScore - minScore) + 1)
      Citizen(citizenId, Person().name, score = score)
    })
    citizens.toList
  }

}
