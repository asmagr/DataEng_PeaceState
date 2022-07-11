package com.peaceland

import org.apache.spark.sql.SparkSession
import com.peaceland.constants.{PeaceConstants => PC}
import org.apache.spark.sql.functions.{col, explode}

object Analytics {

  def main(args: Array[String]): Unit ={
    val spark = SparkSession
      .builder
      .appName("Peacemaker-Analysis")
      .master("local")
      .getOrCreate()

    val df = spark
      .read
      .parquet(PC.PATH + "/reportsFiles/")

    println("Preview DataFrame")
    println(df.show())

    /*
    * Drones which reported most trouble making citizens
    */
    val droneReportedMaxTroubleCitizens = df
      .filter(col(PC.CITIZENS_SCORE) > PC.ALERT_THRESHOLD)
      .select(PC.DRONE_ID,PC.CITIZENS_SCORE)
      .groupBy(PC.DRONE_ID)
      .count()
      .orderBy(col("count").desc)
    println("The drones which reported maximum trouble making citizens: ", droneReportedMaxTroubleCitizens.show(PC.TEN))

    /*
     * Top ten most trouble making citizens
     * Threshold defined at PeaceConstant
     */
    val frequentlyReportedCitizens = df
      .filter(col(PC.CITIZENS_SCORE) > PC.ALERT_THRESHOLD)
      .select(PC.CITIZENS_NAME, PC.CITIZENS_SCORE)
      .groupBy(PC.CITIZENS_NAME)
      .count()
      .orderBy(col("count").desc)

    println("Most trouble making citizens: ", frequentlyReportedCitizens.show(PC.TEN))

  }

}
