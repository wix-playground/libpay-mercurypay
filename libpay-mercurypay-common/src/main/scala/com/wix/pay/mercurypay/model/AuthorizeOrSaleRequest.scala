package com.wix.pay.mercurypay.model

object AccountSources {
  val SWIPED = "Swiped"
  val KEYED = "Keyed"
  val CONTACTLESS = "Contactless"
}

object Frequencies {
  val ONETIME = "OneTime"
  val RECURRING = "Recurring"
}

case class AuthorizeOrSaleRequest(InvoiceNo: String = null,
                                  RefNo: String = null,
                                  Memo: String = null,
                                  Frequency: String = null,
                                  RecordNo: String = null,
                                  EncryptedFormat: String = null,
                                  AccountSource: String = null,
                                  EncryptedBlock: String = null,
                                  EncryptedKey: String = null,
                                  Name: String = null,
                                  Purchase: String = null,
                                  Authorize: String = null,
                                  TerminalName: String = null,
                                  ShiftID: String = null,
                                  OperatorID: String = null,
                                  AcctNo: String = null,
                                  ExpDate: String = null,
                                  Address: String = null,
                                  Zip: String = null,
                                  CVVData: String = null)