package com.wix.pay.mercurypay

case class MercurypayAuthorization(invoiceNo: String,
                                   acctNo: String,
                                   expDate: String,
                                   authCode: String,
                                   acqRefData: String,
                                   authorize: String,
                                   tranCode: String)
