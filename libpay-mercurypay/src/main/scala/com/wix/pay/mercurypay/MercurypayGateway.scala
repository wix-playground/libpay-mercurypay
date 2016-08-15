package com.wix.pay.mercurypay

import com.google.api.client.http._
import com.google.api.client.util.Base64
import com.wix.pay.creditcard.CreditCard
import com.wix.pay.mercurypay.model._
import com.wix.pay.model._
import com.wix.pay.{PaymentErrorException, PaymentException, PaymentGateway, PaymentRejectedException}

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

object Endpoints {
  val production = "https://w1.mercurypay.com/PaymentsAPI/"
  val backup = "https://w2.mercurypay.com/PaymentsAPI/"
  val development = "https://w1.mercurycert.net/PaymentsAPI/"
}

class MercurypayGateway(requestFactory: HttpRequestFactory,
                        connectTimeout: Option[Duration] = None,
                        readTimeout: Option[Duration] = None,
                        numberOfRetries: Int = 0,
                        endpointUrl: String = Endpoints.production,
                        posNameAndVersion: String,
                        merchantParser: MercurypayMerchantParser = new JsonMercurypayMerchantParser,
                        authorizationParser: MercurypayAuthorizationParser = new JsonMercurypayAuthorizationParser) extends PaymentGateway {
  override def authorize(merchantKey: String, creditCard: CreditCard, currencyAmount: CurrencyAmount, customer: Option[Customer], deal: Option[Deal]): Try[String] = {
    Try {
      require(deal.isDefined, "Deal is mandatory for MercuryPay")
      require(deal.get.invoiceId.isDefined, "Deal.invoiceId is mandatory for MercuryPay")
      require(currencyAmount.currency == "USD", s"MercuryPay doesn't support ${currencyAmount.currency}")

      val merchant = merchantParser.parse(merchantKey)

      val request = MercurypayHelper.createAuthorizeRequest(posNameAndVersion, deal.get.invoiceId.get, creditCard, currencyAmount)
      val requestJson = AuthorizeOrSaleRequestParser.stringify(request)
      val responseJson = doJsonRequest("Credit/PreAuth", merchant.merchantId, merchant.password, requestJson)
      val response = AuthorizeOrSaleResponseParser.parse(responseJson)

      verifyResponse(response)

      val authorization = MercurypayAuthorization(
        invoiceNo = response.InvoiceNo.get,
        acctNo = response.AcctNo.get,
        expDate = response.ExpDate.get,
        authCode = response.AuthCode.get,
        acqRefData = response.AcqRefData.get,
        authorize = response.Authorize.get
      )
      authorizationParser.stringify(authorization)
    } match {
      case Success(authorizationKey) => Success(authorizationKey)
      case Failure(e: PaymentException) => Failure(e)
      case Failure(e) => Failure(PaymentErrorException(e.getMessage, e))
    }
  }

  override def capture(merchantKey: String, authorizationKey: String, amount: Double): Try[String] = {
    Try {
      val merchant = merchantParser.parse(merchantKey)
      val authorization = authorizationParser.parse(authorizationKey)

      val request = MercurypayHelper.createCaptureRequest(
        invoiceNo = authorization.invoiceNo,
        authorize = authorization.authorize,
        acctNo = authorization.acctNo,
        expDate = authorization.expDate,
        authCode = authorization.authCode,
        acqRefData = authorization.acqRefData,
        amount = amount)
      val requestJson = CaptureRequestParser.stringify(request)
      val responseJson = doJsonRequest("Credit/PreAuthCapture", merchant.merchantId, merchant.password, requestJson)
      val response = CaptureResponseParser.parse(responseJson)

      verifyResponse(response)
      if (response.CaptureStatus.get != CaptureStatuses.captured) {
        throw PaymentRejectedException(s"${response.TextResponse.get} (CaptureStatus=${response.CaptureStatus.get})")
      }

      response.RefNo.get
    } match {
      case Success(transactionId) => Success(transactionId)
      case Failure(e: PaymentException) => Failure(e)
      case Failure(e) => Failure(PaymentErrorException(e.getMessage, e))
    }
  }

  override def sale(merchantKey: String, creditCard: CreditCard, currencyAmount: CurrencyAmount, customer: Option[Customer], deal: Option[Deal]): Try[String] = {
    Try {
      require(deal.isDefined, "Deal is mandatory for MercuryPay")
      require(deal.get.invoiceId.isDefined, "Deal.invoiceId is mandatory for MercuryPay")
      require(currencyAmount.currency == "USD", s"MercuryPay doesn't support ${currencyAmount.currency}")

      val merchant = merchantParser.parse(merchantKey)

      val request = MercurypayHelper.createSaleRequest(posNameAndVersion, deal.get.invoiceId.get, creditCard, currencyAmount)
      val requestJson = AuthorizeOrSaleRequestParser.stringify(request)
      val responseJson = doJsonRequest("Credit/Sale", merchant.merchantId, merchant.password, requestJson)
      val response = AuthorizeOrSaleResponseParser.parse(responseJson)

      verifyResponse(response)

      response.RefNo.get
    } match {
      case Success(transactionId) => Success(transactionId)
      case Failure(e: PaymentException) => Failure(e)
      case Failure(e) => Failure(PaymentErrorException(e.getMessage, e))
    }
  }

  override def voidAuthorization(merchantKey: String, authorizationKey: String): Try[String] = {
    Try {
      // TODO: implement "Reversal Request" using VoidSale
      val authorization = authorizationParser.parse(authorizationKey)
      "" // TODO: use response's RefNo
    }
  }

  private def verifyResponse(response: Response): Unit = {
    response.CmdStatus.get match {
      case CmdStatuses.error => throw PaymentErrorException(s"${response.DSIXReturnCode.get}|${response.TextResponse.get}")
      case CmdStatuses.declined => throw PaymentRejectedException(s"${response.TextResponse.get} (CmdStatus=${CmdStatuses.declined})")
      case CmdStatuses.approved => // Nothing to do
      case otherCmdStatus => throw PaymentErrorException(s"Unexpected CmdStatus=$otherCmdStatus")
    }
  }

  private def doJsonRequest(resource: String, username: String, password: String, requestJson: String): String = {
    val httpRequest = requestFactory.buildPostRequest(
      new GenericUrl(s"$endpointUrl$resource"),
      new ByteArrayContent("application/json", requestJson.getBytes("UTF-8"))
    )

    connectTimeout foreach (to => httpRequest.setConnectTimeout(to.toMillis.toInt))
    readTimeout foreach (to => httpRequest.setReadTimeout(to.toMillis.toInt))
    httpRequest.setNumberOfRetries(numberOfRetries)

    httpRequest.getHeaders.setAuthorization(s"Basic ${Base64.encodeBase64String(s"$username:$password".getBytes("UTF-8"))}")

    executeRequest(httpRequest)
  }

  private def executeRequest(httpRequest: HttpRequest): String = {
    val httpResponse = httpRequest.execute()
    try {
      httpResponse.parseAsString()
    } finally {
      httpResponse.ignore()
    }
  }
}
