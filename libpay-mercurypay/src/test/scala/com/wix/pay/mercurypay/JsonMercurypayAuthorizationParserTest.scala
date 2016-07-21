package com.wix.pay.mercurypay


import com.wix.pay.mercurypay.MercurypayMatchers._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope


class JsonMercurypayAuthorizationParserTest extends SpecWithJUnit {
  trait Ctx extends Scope {
    val authorizationParser: MercurypayAuthorizationParser = new JsonMercurypayAuthorizationParser
  }

  "stringify and then parse" should {
    "yield an authorization similar to the original one" in new Ctx {
      val someAuthorization = MercurypayAuthorization(
        invoiceNo = "some invoiceNo",
        acctNo = "some acctNo",
        expDate = "some expDate",
        authCode = "some authCode",
        acqRefData = "some acqRefData",
        authorize = "some authorize"
      )

      val authorizationKey = authorizationParser.stringify(someAuthorization)
      authorizationParser.parse(authorizationKey) must beAuthorization(
        invoiceNo = ===(someAuthorization.invoiceNo),
        acctNo = ===(someAuthorization.acctNo),
        expDate = ===(someAuthorization.expDate),
        authCode = ===(someAuthorization.authCode),
        acqRefData = ===(someAuthorization.acqRefData),
        authorize = ===(someAuthorization.authorize)
      )
    }
  }
}
