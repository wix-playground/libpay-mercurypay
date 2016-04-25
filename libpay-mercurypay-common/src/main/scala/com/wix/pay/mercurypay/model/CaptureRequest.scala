package com.wix.pay.mercurypay.model

case class CaptureRequest(InvoiceNo: String,
                          Purchase: String,
                          Authorize: String,
                          AcctNo: String,
                          ExpDate: String,
                          AuthCode: String,
                          AcqRefData: String)
