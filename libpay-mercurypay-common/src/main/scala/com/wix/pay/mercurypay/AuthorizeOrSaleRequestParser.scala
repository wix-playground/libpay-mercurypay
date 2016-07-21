package com.wix.pay.mercurypay

import com.wix.pay.mercurypay.model.AuthorizeOrSaleRequest
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

object AuthorizeOrSaleRequestParser {
  private implicit val formats = DefaultFormats

  def parse(str: String): AuthorizeOrSaleRequest = {
    Serialization.read[AuthorizeOrSaleRequest](str)
  }

  def stringify(obj: AuthorizeOrSaleRequest): String = {
    Serialization.write(obj)
  }
}
