package com.wix.pay.mercurypay

trait MercurypayMerchantParser {
  def parse(merchantKey: String): MercurypayMerchant
  def stringify(merchant: MercurypayMerchant): String
}
