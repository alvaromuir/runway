package com.verizon.itanalytics.dataengineering.runway.microservice.utils

import spray.json._

trait Utils {
  object Listify {
    def apply(json: JsValue): List[Any] = listify(json)

    def listify(json: JsValue): List[Any] = {
      val observation = json.toString.stripMargin.parseJson.asJsObject
      val featuresList = observation.getClass.getDeclaredFields.map(_.getName).zip(observation.productIterator.to).toMap.get("fields")
        .head
        .asInstanceOf[Map[String, Any]]
        .values
        .toList
      return featuresList
    }

  }


  //sourced from https://gist.github.com/sam/5213151

  object Slugify {
    def apply(input:String): String = slugify(input)

    def slugify(input: String): String = {
      import java.text.Normalizer
      Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("[^\\w\\s-]", "") // Remove all non-word, non-space or non-dash characters
        .replace('-', ' ')            // Replace dashes with spaces
        .replace("\n", "").replace("\r", "") // remove newline characters
        .trim                         // Trim leading/trailing whitespace (including what used to be leading/trailing dashes)
        .replaceAll("\\s+", "-")      // Replace whitespace (including newlines and repetitions) with single dashes
        .toLowerCase                  // Lowercase the final results
    }
  }
}
