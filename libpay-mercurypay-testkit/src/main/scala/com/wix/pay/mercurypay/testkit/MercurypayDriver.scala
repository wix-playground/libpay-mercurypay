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

  private val authorizeOrSaleRequestParser = new AuthorizeOrSaleRequestParser
  private val authorizeOrSaleResponseParser = new AuthorizeOrSaleResponseParser
  private val captureRequestParser = new CaptureRequestParser
  private val captureResponseParser = new CaptureResponseParser

  def startProbe() {
    probe.doStart()
  }

  def stopProbe() {
    probe.doStop()
  }

  def resetProbe() {
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
      val actualRequest = authorizeOrSaleRequestParser.parse(entity.asString)
      request == actualRequest
    }

    private def returns(response: AuthorizeOrSaleResponse): Unit = {
      returns(StatusCodes.OK, authorizeOrSaleResponseParser.stringify(response))
    }

    def returns(transactionId: String): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = CmdStatuses.APPROVED,
        TranCode = transactionId
      )
      returns(response)
    }

    def isRejected(): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = CmdStatuses.DECLINED,
        TextResponse = "some text response"
      )
      returns(response)
    }
  }

  class AuthorizeRequestCtx(username: String,
                            password: String,
                            request: AuthorizeOrSaleRequest) extends Ctx("/Credit/PreAuth", username, password) {

    protected override def verifyContent(entity: HttpEntity): Boolean = {
      val actualRequest = authorizeOrSaleRequestParser.parse(entity.asString)
      request == actualRequest
    }

    private def returns(response: AuthorizeOrSaleResponse): Unit = {
      returns(StatusCodes.OK, authorizeOrSaleResponseParser.stringify(response))
    }

    def returns(invoiceNo: String,
                acctNo: String,
                expDate: String,
                authCode: String,
                acqRefData: String,
                authorize: String,
                tranCode: String): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = CmdStatuses.APPROVED,
        InvoiceNo = invoiceNo,
        AcctNo = acctNo,
        ExpDate = expDate,
        AuthCode = authCode,
        AcqRefData = acqRefData,
        Authorize = authorize,
        TranCode = tranCode
      )
      returns(response)
    }

    def isRejected(): Unit = {
      val response = AuthorizeOrSaleResponse(
        CmdStatus = CmdStatuses.DECLINED,
        TextResponse = "some text response"
      )
      returns(response)
    }
  }

  class CaptureRequestCtx(username: String,
                          password: String,
                          request: CaptureRequest) extends Ctx("/Credit/PreAuthCapture", username, password) {

    protected override def verifyContent(entity: HttpEntity): Boolean = {
      val actualRequest = captureRequestParser.parse(entity.asString)
      request == actualRequest
    }

    private def returns(response: CaptureResponse): Unit = {
      returns(StatusCodes.OK, captureResponseParser.stringify(response))
    }

    def returns(transactionId: String): Unit = {
      val response = CaptureResponse(
        CmdStatus = CmdStatuses.APPROVED,
        CaptureStatus = CaptureStatuses.CAPTURED,
        TranCode = transactionId
      )
      returns(response)
    }
  }
}
