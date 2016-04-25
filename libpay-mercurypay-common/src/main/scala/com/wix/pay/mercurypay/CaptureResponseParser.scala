package com.wix.pay.mercurypay

import com.wix.pay.mercurypay.model.CaptureResponse
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class CaptureResponseParser {
  private implicit val formats = DefaultFormats

  def parse(str: String): CaptureResponse = {
    Serialization.read[CaptureResponse](str)
  }

  def stringify(obj: CaptureResponse): String = {
    Serialization.write(obj)
  }
}
