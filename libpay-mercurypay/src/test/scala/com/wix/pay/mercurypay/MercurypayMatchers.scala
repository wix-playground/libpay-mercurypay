package com.wix.pay.mercurypay

import org.specs2.matcher.{AlwaysMatcher, Matcher, Matchers}

trait MercurypayMatchers extends Matchers {
  def authorizationParser: MercurypayAuthorizationParser

  def beAuthorization(invoiceNo: Matcher[String] = AlwaysMatcher(),
                      acctNo: Matcher[String] = AlwaysMatcher(),
                      expDate: Matcher[String] = AlwaysMatcher(),
                      authCode: Matcher[String] = AlwaysMatcher(),
                      acqRefData: Matcher[String] = AlwaysMatcher(),
                      authorize: Matcher[String] = AlwaysMatcher(),
                      tranCode: Matcher[String] = AlwaysMatcher()): Matcher[MercurypayAuthorization] = {
    invoiceNo ^^ { (_: MercurypayAuthorization).invoiceNo aka "invoiceNo" } and
      acctNo ^^ { (_: MercurypayAuthorization).acctNo aka "acctNo" } and
      expDate ^^ { (_: MercurypayAuthorization).expDate aka "expDate" } and
      authCode ^^ { (_: MercurypayAuthorization).authCode aka "authCode" } and
      acqRefData ^^ { (_: MercurypayAuthorization).acqRefData aka "acqRefData" } and
      authorize ^^ { (_: MercurypayAuthorization).authorize aka "authorize" } and
      tranCode ^^ { (_: MercurypayAuthorization).tranCode aka "tranCode" }
  }

  def beAuthorizationKey(authorization: Matcher[MercurypayAuthorization]): Matcher[String] = {
    authorization ^^ { authorizationParser.parse(_: String) aka "parsed authorization"}
  }
}

object MercurypayMatchers extends MercurypayMatchers {
  override val authorizationParser = new JsonMercurypayAuthorizationParser()
}