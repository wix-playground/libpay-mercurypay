package com.wix.pay.mercurypay.model

trait Response {
  val ResponseOrigin: Option[String]
  val DSIXReturnCode: Option[String]
  val CmdStatus: Option[String]
  val TextResponse: Option[String]
}
