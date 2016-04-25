package com.wix.pay.mercurypay

trait MercurypayAuthorizationParser {
  def parse(authorizationKey: String): MercurypayAuthorization
  def stringify(authorization: MercurypayAuthorization): String
}
