package com.wix.pay.mercurypay

import com.wix.pay.mercurypay.model.AuthorizeOrSaleResponse
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class AuthorizeOrSaleResponseParser {
  private implicit val formats = DefaultFormats

  def parse(str: String): AuthorizeOrSaleResponse = {
    Serialization.read[AuthorizeOrSaleResponse](str)
  }

  def stringify(obj: AuthorizeOrSaleResponse): String = {
    Serialization.write(obj)
  }
}
