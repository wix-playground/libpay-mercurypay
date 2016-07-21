package com.wix.pay.mercurypay

import com.wix.pay.creditcard.CreditCard
import com.wix.pay.mercurypay.model.Conversions._
import com.wix.pay.mercurypay.model.{AccountSources, AuthorizeOrSaleRequest, CaptureRequest, Frequencies}
import com.wix.pay.model.CurrencyAmount


object MercurypayHelper {
  def createSaleRequest(posNameAndVersion: String, invoiceId: String, creditCard: CreditCard, currencyAmount: CurrencyAmount): AuthorizeOrSaleRequest = {
    AuthorizeOrSaleRequest(
      InvoiceNo = invoiceId,
      RefNo = "0001", // placeholder, per MercuryPay documentation
      Memo = posNameAndVersion,
      Frequency = Frequencies.onetime,
      AccountSource = AccountSources.keyed,
      Purchase = toMercurypayAmount(currencyAmount.amount),
      AcctNo = creditCard.number,
      ExpDate = toMercurypayYearMonth(
        year = creditCard.expiration.year,
        month = creditCard.expiration.month
      ),
      Address = creditCard.billingAddress.orNull,
      Zip = creditCard.billingPostalCode.orNull,
      CVVData = creditCard.csc.orNull)
  }

  def createAuthorizeRequest(posNameAndVersion: String, invoiceId: String, creditCard: CreditCard, currencyAmount: CurrencyAmount): AuthorizeOrSaleRequest = {
    AuthorizeOrSaleRequest(
      InvoiceNo = invoiceId,
      RefNo = "0001", // placeholder, per MercuryPay documentation
      Memo = posNameAndVersion,
      Frequency = Frequencies.onetime,
      AccountSource = AccountSources.keyed,
      Purchase = toMercurypayAmount(currencyAmount.amount),
      Authorize = toMercurypayAmount(currencyAmount.amount),
      AcctNo = creditCard.number,
      ExpDate = toMercurypayYearMonth(
        year = creditCard.expiration.year,
        month = creditCard.expiration.month
      ),
      Address = creditCard.billingAddress.orNull,
      Zip = creditCard.billingPostalCode.orNull,
      CVVData = creditCard.csc.orNull)
  }

  def createCaptureRequest(invoiceNo: String,
                           authorize: String,
                           acctNo: String,
                           expDate: String,
                           authCode: String,
                           acqRefData: String,
                           amount: Double): CaptureRequest = {
    CaptureRequest(
      InvoiceNo = invoiceNo,
      Purchase = toMercurypayAmount(amount),
      Authorize = authorize,
      AcctNo = acctNo,
      ExpDate =  expDate,
      AuthCode = authCode,
      AcqRefData = acqRefData
    )
  }
}
