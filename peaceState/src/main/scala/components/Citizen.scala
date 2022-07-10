package components

import io.alphash.faker.Person
import scala.collection.mutable.MutableList

import scala.util.Random

case class Citizen(id: Int, name: String, score: Int)

object Citizen {
  val maxScore = 100
  val minScore = 10
  def createCitizens(n: Int): List[Citizen] ={
    val citizens: MutableList[Citizen] = MutableList()
    for(i <- 1 to n){
      val score = Random.nextInt( (maxScore - minScore) + 1)
      citizens += Citizen(i, Person().name, score = score)
    }
    citizens.toList
  }

}
