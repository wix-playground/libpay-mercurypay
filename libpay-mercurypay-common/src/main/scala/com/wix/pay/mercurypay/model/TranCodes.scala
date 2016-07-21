package com.wix.pay.mercurypay.model

object TranCodes {
  /** Activates the account and loads a specified amount value onto the card. */
  val issue = "Issue"
  /**
    * Takes value off of the card, reducing the available balance by that purchase amount.
    * Declines the transaction if there is insufficient balance.
    */
  val sale = "Sale"
  /**
    * Takes value off of the card, reducing the available balance by that purchase amount.
    * If there is an insufficient account balance, the transaction will approve for the remaining balance on the card.
    */
  val noNSFSale = "NoNSFSale"
  /**
    * Adds value to a card, increasing the balance.
    * Typically used for a gift inventory line item.
    */
  val reload = "Reload"
  /**
    * Adds value to a card, increasing the balance.
    * Typically used when merchandise is returned and the value is added to a gift card in place of cash.
    */
  val `return` = "Return"
  /** Cancels a previously approved Sale, adding the voided amount back to the card. */
  val voidSale = "VoidSale"
  /** Cancels a previously approved Reload, adding the voided amount back to the card. */
  val voidReload = "VoidReload"
  /** Cancels a previously approved Return, adding the voided amount back to the card. */
  val voidReturn = "VoidReturn"
  /** Cancels the issuance of a card, returning the account balance to 0.00 and puts the card into a non-issued state. */
  val voidIssue = "VoidIssue"
  /** A balance inquiry returns the remaining balance of a card. */
  val balance = "Balance"
  val preAuth = "PreAuth"
  val preAuthCapture = "PreAuthCapture"
}
