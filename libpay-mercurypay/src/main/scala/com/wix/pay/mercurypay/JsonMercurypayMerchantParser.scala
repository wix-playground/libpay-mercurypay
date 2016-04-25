package com.wix.pay.mercurypay

import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class JsonMercurypayMerchantParser() extends MercurypayMerchantParser {
  private implicit val formats = DefaultFormats

  override def parse(merchantKey: String): MercurypayMerchant = {
    Serialization.read[MercurypayMerchant](merchantKey)
  }

  override def stringify(merchant: MercurypayMerchant): String = {
    Serialization.write(merchant)
  }
}
