package com.wix.pay.mercurypay.model

object ResponseOrigins {
  val CLIENT = "Client"
  val PROCESSOR = "Processor"
}

object CmdStatuses {
  val ERROR = "Error"
  val APPROVED = "Approved"
  val DECLINED = "Declined"
}

trait Response {
  val ResponseOrigin: String
  val DSIXReturnCode: String
  val CmdStatus: String
  val TextResponse: String
}
