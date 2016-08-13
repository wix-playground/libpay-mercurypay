package com.wix.pay.mercurypay.model

trait Response {
  def ResponseOrigin: Option[String]
  def DSIXReturnCode: Option[String]
  def CmdStatus: Option[String]
  def TextResponse: Option[String]
}
