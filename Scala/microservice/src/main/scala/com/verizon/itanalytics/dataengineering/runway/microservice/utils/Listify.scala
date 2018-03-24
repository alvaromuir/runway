package com.verizon.itanalytics.dataengineering.runway.microservice.utils

import spray.json._

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
