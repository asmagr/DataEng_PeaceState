package com.peaceland

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.peaceland.constants.{PeaceConstants => PC}
import org.apache.spark.sql.functions.{arrays_zip, col, explode, from_json}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{ArrayType, DoubleType, IntegerType, StringType, StructType}

object Storage {

   def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Spark-Kafka-App")
      .master("local")
      .getOrCreate()

    val kafkaDF: DataFrame = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", PC.TOPIC)
      .option("startingOffsets", "earliest")
      .load()

    val schema = new StructType()
      .add(PC.DRONE_ID, IntegerType, nullable = true)
      .add(PC.LATITUDE, DoubleType, nullable = true)
      .add(PC.LONGITUDE, DoubleType, nullable = true)
      .add(PC.CITIZENS_ID, ArrayType(IntegerType), nullable = true)
      .add(PC.CITIZENS_SCORE, ArrayType(IntegerType), nullable = true)
      .add(PC.CITIZENS_NAME, ArrayType(StringType), nullable = true)
      .add(PC.WORDS, StringType, nullable = true)


    val finalDf = kafkaDF
      .select(
        from_json(col("value").cast("string"), schema).as(PC.REPORT),
        col("timestamp").as("load_ts")
      )
      .withColumn(PC.DRONE_ID, col(s"${PC.REPORT}.${PC.DRONE_ID}"))
      .withColumn(PC.LATITUDE, col(s"${PC.REPORT}.${PC.LATITUDE}"))
      .withColumn(PC.LONGITUDE, col(s"${PC.REPORT}.${PC.LONGITUDE}"))
      .withColumn(PC.CITIZENS, explode(
        arrays_zip(
          col(s"${PC.REPORT}.${PC.CITIZENS_ID}"),
          col(s"${PC.REPORT}.${PC.CITIZENS_SCORE}"),
          col(s"${PC.REPORT}.${PC.CITIZENS_NAME}")
        )
      ))
      .withColumn(PC.CITIZENS_ID, col(PC.CITIZENS).getField("0"))
      .withColumn(PC.CITIZENS_SCORE, col(PC.CITIZENS).getField("1"))
      .withColumn(PC.CITIZENS_NAME, col(PC.CITIZENS).getField("2"))
      .withColumn(PC.WORDS, col(s"${PC.REPORT}.${PC.WORDS}"))
      .drop(PC.REPORT, PC.CITIZENS)

    finalDf
      .writeStream
      .trigger(Trigger.ProcessingTime(10000))
      .format("parquet")
      .option("checkpointLocation", PC.PATH + "/checkpoint/")
      .option("path", PC.PATH + "/reportsFiles/")
      .option("delimiter", ";")
      .option("header", false)
      .option("failOnDataLoss", "false")
      .start()
      .awaitTermination()

  }
}
