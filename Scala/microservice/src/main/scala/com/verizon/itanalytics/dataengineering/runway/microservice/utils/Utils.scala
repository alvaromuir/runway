package com.verizon.itanalytics.dataengineering.runway.microservice.utils

import com.verizon.itanalytics.dataengineering.runway.microservice.Tables.{db, models}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future


trait Utils {

  object Listify {
    implicit val formats = DefaultFormats

    def apply(json: String): List[Any] = listify(json)

    /**
      * Returns a list of values from JSON input key-pairs
      *
      * @param json json String value
      * @return String
      */
    def listify(json: String): List[Any] = {
      val observation = parse(json.toString.stripMargin)
      observation.getClass.getDeclaredFields.map(_.getName).zip(observation.productIterator.to).toMap.get("fields")
        .head
        .asInstanceOf[Map[String, Any]]
        .values
        .toList
    }

  }


  //sourced from https://gist.github.com/sam/5213151

  object Slugify {
    def apply(input: String): String = slugify(input)

    /**
      * Returns a url-safe string from string input
      *
      * @param input String
      * @return String
      */
    def slugify(input: String): String = {
      import java.text.Normalizer
      Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("[^\\w\\s-]", "") // Remove all non-word, non-space or non-dash characters
        .replace('-', ' ') // Replace dashes with spaces
        .replace("\n", "").replace("\r", "") // remove newline characters
        .trim // Trim leading/trailing whitespace (including what used to be leading/trailing dashes)
        .replaceAll("\\s+", "-") // Replace whitespace (including newlines and repetitions) with single dashes
        .toLowerCase // Lowercase the final results
    }
  }

  def isNumeric(s: String): Boolean = {
    try {
      s.toInt
      true
    } catch {
      case e: Exception => false
    }
  }



  object CheckExistence {
    def apply(input: String): Future[Boolean] = checkExistence(input)

    def checkExistence(input: String): Future[Boolean] = {
      db.run((for {
        m <- models
        if m.name === input
      } yield m).exists.result)
    }
  }


  object Versionize {
    def apply(input: String): String = versionize(input)

    /**input:String)
      * Returns a versioned numbering to a string
      *
      * @param input String
      * @return String
      */
    def versionize(input: String): String = {
      val nameTail: String = input.splitAt(input.lastIndexOf("-"))._2.substring(1)
      if (isNumeric(nameTail)) s"$input-${nameTail.toInt + 1}" else s"$input-1"
    }
  }

}

