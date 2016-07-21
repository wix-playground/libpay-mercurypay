package com.wix.pay.mercurypay.testkit

import com.google.api.client.util.Base64
import com.wix.hoopoe.http.testkit.EmbeddedHttpProbe
import com.wix.pay.creditcard.CreditCard
import com.wix.pay.mercurypay._
import com.wix.pay.mercurypay.model._
import com.wix.pay.model.CurrencyAmount
import spray.http._


class MercurypayDriver(port: Int) {
  private val probe = new EmbeddedHttpProbe(port, EmbeddedHttpProbe.NotFoundHandler)

  def start() {
    probe.doStart()
  }

  def stop() {
    probe.doStop()
  }

  def reset() {
    probe.handlers.clear()
  }

  def aSaleRequestFor(username: String,
                      password: String,
                      posNameAndVersion: String,
                      invoiceId: String,
                      creditCard: CreditCard,
                      currencyAmount: CurrencyAmount): SaleRequestCtx = {
    val request = MercurypayHelper.createSaleRequest(
      posNameAndVersion = posNameAndVersion,
      invoiceId = invoiceId,
      creditCard = creditCard,
      currencyAmount = currencyAmount)

    new SaleRequestCtx(
      username = username,
      password = password,
      request = request
    )
  }

  def anAuthorizeRequestFor(username: String,
                            password: String,
                            posNameAndVersion: String,
                            invoiceId: String,
                            creditCard: CreditCard,
                            currencyAmount: CurrencyAmount): AuthorizeRequestCtx = {
    val request = MercurypayHelper.createAuthorizeRequest(
      posNameAndVersion = posNameAndVersion,
      invoiceId = invoiceId,
      creditCard = creditCard,
      currencyAmount = currencyAmount)

    new AuthorizeRequestCtx(
      username = username,
      password = password,
      request = request
    )
  }

  def aCaptureRequestFor(username: String,
                         password: String,
                         invoiceNo: String,
                         authorize: String,
                         acctNo: String,
                         expDate: String,
                         authCode: String,
                         acqRefData: String,
                         amount: Double): CaptureRequestCtx = {
    val request = MercurypayHelper.createCaptureRequest(
      invoiceNo = invoiceNo,
      authorize = authorize,
      acctNo = acctNo,
      expDate = expDate,
      authCode = authCode,
      acqRefData = acqRefData,
      amount = amount)

    new CaptureRequestCtx(
      username = username,
      password = password,
      request = request
    )
  }

  abstract class Ctx(val resource: String, username: String, password: String) {
    def isStubbedRequestEntity(entity: HttpEntity, headers: List[HttpHeader]): Boolean = {
      isAuthorized(headers) && verifyContent(entity)
    }

    private def isAuthorized(headers: List[HttpHeader]): Boolean = {
      val expectedValue = s"Basic ${Base64.encodeBase64String(s"$username:$password".getBytes("UTF-8"))}"
      headers.exists( h => h.name == "Authorization" && h.value == expectedValue)
    }

    protected def verifyContent(entity: HttpEntity): Boolean

    protected def returns(statusCode: StatusCode, responseJson: String) {
      probe.handlers += {
        case HttpRequest(
        HttpMethods.POST,
        Uri.Path(`resource`),
        headers,
        entity,
        _) if isStubbedRequestEntity(entity, headers) =>
          HttpResponse(
            status = statusCode,
            entity = HttpEntity(ContentTypes.`application/json`, responseJson))
      }
    }
  }

  class SaleRequestCtx(username: String,
                       password: String,
                       request: AuthorizeOrSaleRequest) extends Ctx("/Credit/Sale", username, password) {

    protected override def verifyContent(entity: HttpEntity): Boolean = {
      val actualRequest = AuthorizeOrSaleRequestParser.parse(entity.asString)
      request == actualRequest
    }

    private def returns(response: AuthorizeOrSaleResponse): Unit = {
      returns(StatusCodes.OK, AuthorizeOrSaleResponseParser.stringify(response))
    }

    def returns(transactionId: String): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = Some(CmdStatuses.approved),
        TranCode = Some(TranCodes.sale),
        RefNo = Some(transactionId)
      )
      returns(response)
    }

    def isRejected(): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = Some(CmdStatuses.declined),
        TranCode = Some(TranCodes.sale),
        TextResponse = Some("some text response")
      )
      returns(response)
    }
  }

  class AuthorizeRequestCtx(username: String,
                            password: String,
                            request: AuthorizeOrSaleRequest) extends Ctx("/Credit/PreAuth", username, password) {

    protected override def verifyContent(entity: HttpEntity): Boolean = {
      val actualRequest = AuthorizeOrSaleRequestParser.parse(entity.asString)
      request == actualRequest
    }

    private def returns(response: AuthorizeOrSaleResponse): Unit = {
      returns(StatusCodes.OK, AuthorizeOrSaleResponseParser.stringify(response))
    }

    def returns(invoiceNo: String,
                acctNo: String,
                expDate: String,
                authCode: String,
                acqRefData: String,
                authorize: String): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = Some(CmdStatuses.approved),
        InvoiceNo = Some(invoiceNo),
        AcctNo = Some(acctNo),
        ExpDate = Some(expDate),
        AuthCode = Some(authCode),
        AcqRefData = Some(acqRefData),
        Authorize = Some(authorize),
        TranCode = Some(TranCodes.preAuth)
      )
      returns(response)
    }

    def isRejected(): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = Some(CmdStatuses.declined),
        TranCode = Some(TranCodes.preAuth),
        TextResponse = Some("some text response")
      )
      returns(response)
    }
  }

  class CaptureRequestCtx(username: String,
                          password: String,
                          request: CaptureRequest) extends Ctx("/Credit/PreAuthCapture", username, password) {

    protected override def verifyContent(entity: HttpEntity): Boolean = {
      val actualRequest = CaptureRequestParser.parse(entity.asString)
      request == actualRequest
    }

    private def returns(response: CaptureResponse): Unit = {
      returns(StatusCodes.OK, CaptureResponseParser.stringify(response))
    }

    def returns(transactionId: String): Unit = {
      val response = CaptureResponse(
        CmdStatus = Some(CmdStatuses.approved),
        CaptureStatus = Some(CaptureStatuses.captured),
        TranCode = Some(TranCodes.preAuthCapture),
        RefNo = Some(transactionId)
      )
      returns(response)
    }
  }
}
