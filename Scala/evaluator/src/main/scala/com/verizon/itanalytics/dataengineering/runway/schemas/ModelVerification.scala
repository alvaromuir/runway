package com.verizon.itanalytics.dataengineering.runway.schemas

// http://dmg.org/pmml/v4-3/ModelVerification.html
trait ModelVerification {
  case class ModelVerification(recordCount: Option[Int] = None,
                               fieldCount: Option[Int] = None,
                               verificationFields: Option[VerificationFields] = None)

  case class VerificationFields(verificationField: Option[Seq[VerificationField]] = None)

  case class VerificationField(field: String,
                               column: Option[String] = None,
                               precision: Double = 1E-6,
                               zeroThreshold: Double = 1E-6)
}
