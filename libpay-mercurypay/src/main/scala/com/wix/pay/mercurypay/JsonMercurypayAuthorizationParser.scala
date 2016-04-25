package com.wix.pay.mercurypay

import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class JsonMercurypayAuthorizationParser() extends MercurypayAuthorizationParser {
  private implicit val formats = DefaultFormats

  override def parse(authorizationKey: String): MercurypayAuthorization = {
    Serialization.read[MercurypayAuthorization](authorizationKey)
  }

  override def stringify(authorization: MercurypayAuthorization): String = {
    Serialization.write(authorization)
  }
}
