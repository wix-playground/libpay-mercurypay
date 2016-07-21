package com.wix.pay.mercurypay

import com.wix.pay.mercurypay.model.CaptureRequest
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

object CaptureRequestParser {
  private implicit val formats = DefaultFormats

  def parse(str: String): CaptureRequest = {
    Serialization.read[CaptureRequest](str)
  }

  def stringify(obj: CaptureRequest): String = {
    Serialization.write(obj)
  }
}
