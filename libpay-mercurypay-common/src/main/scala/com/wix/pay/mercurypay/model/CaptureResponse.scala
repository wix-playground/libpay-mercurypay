package com.wix.pay.mercurypay.model

object CaptureStatuses {
  val CAPTURED = "Captured"
}

case class CaptureResponse(ResponseOrigin: String = null,
                           DSIXReturnCode: String = null,
                           CmdStatus: String = null,
                           TextResponse: String = null,
                           UserTraceData: String = null,
                           MerchantID: String = null,
                           AcctNo: String = null,
                           ExpDate: String = null,
                           CardType: String = null,
                           TranCode: String = null,
                           AuthCode: String = null,
                           CaptureStatus: String = null,
                           RefNo: String = null,
                           InvoiceNo: String = null,
                           Purchase: String = null,
                           Authorize: String = null,
                           AcqRefData: String = null,
                           ProcessData: String = null) extends Response