package com.wix.pay.mercurypay.model

import java.text.DecimalFormat

object Conversions {
  private def amountFormat = new DecimalFormat("0.00")

  def toMercurypayAmount(amount: Double): String = {
    amountFormat.format(amount)
  }

  def toMercurypayYearMonth(year: Int, month: Int): String = {
    f"$month%02d${year % 100}%02d"
  }
}
