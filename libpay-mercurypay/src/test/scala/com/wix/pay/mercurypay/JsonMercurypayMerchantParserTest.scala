package com.wix.pay.mercurypay


import org.specs2.matcher.{AlwaysMatcher, Matcher}
import org.specs2.matcher.MustMatchers._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope


class JsonMercurypayMerchantParserTest extends SpecWithJUnit {
  trait Ctx extends Scope {
    val merchantParser: MercurypayMerchantParser = new JsonMercurypayMerchantParser
  }

  def beMercurypayMerchant(merchantId: Matcher[String] = AlwaysMatcher(),
                           password: Matcher[String] = AlwaysMatcher()): Matcher[MercurypayMerchant] = {
    merchantId ^^ { (_: MercurypayMerchant).merchantId aka "merchantId" } and
      password ^^ { (_: MercurypayMerchant).password aka "password" }
  }

  "stringify and then parse" should {
    "yield a merchant similar to the original one" in new Ctx {
      val someMerchant = MercurypayMerchant(
        merchantId = "some merchant ID",
        password = "some password"
      )

      val merchantKey = merchantParser.stringify(someMerchant)
      merchantParser.parse(merchantKey) must beMercurypayMerchant(
        merchantId = ===(someMerchant.merchantId),
        password = ===(someMerchant.password)
      )
    }
  }
}
