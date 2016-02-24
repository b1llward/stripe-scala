package com.vpayproc
/*
by Anurag Goel and Bill Ward.

* 
*/


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future,Await}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import scala.util.Properties

import play.api.mvc._
import play.api.libs.ws._

import net.liftweb.json
import net.liftweb.json.JsonDSL._

import play.api.libs.json.Json

//represents Errors returned as JSON
case class ErrorContainer(error: Error)
case class Error(`type`: String, message: String, code: Option[String], param: Option[String])


case class PayCardModel (
  id: String,
  //account: Option[String] = None,
  addressCity: Option[String] = None,
  addressCountry: Option[String] = None,  
  addressLine1: Option[String] = None,
  addressLine1Check: Option[String] = None,
  addressLine2: Option[String] = None,
  addressState: Option[String] = None,
  addressZip: Option[String] = None,
  addressZipCheck: Option[String] = None,
  brand: Option[String] = None,
  country: Option[String] = None,
  //currency: Option[String] = None,
  customer: Option[String] = None,
  cvcCheck: Option[String] = None,
  //defaultForCurrency: Option[String] = None,
  dynamicLast4: Option[String] = None,
  expMonth: Int,
  expYear: Int,
  fingerprint: String,
  funding: Option[String] = None,
  last4: String,
  name: Option[String] = None,
  recipient: Option[String] = None,
  tokenizationMethod: Option[String] = None
  )


object PayCardModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayCardModel]

}
case class PayCardCollectionModel(count: Int, data: List[PayCardModel])
object PayCardCollectionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayCardCollectionModel]

}

case class PaySourceModel (
  id: String,
  addressCity: Option[String] = None,
  addressCountry: Option[String] = None,
  addressLine1: Option[String] = None,
  addressLine1Check: Option[String] = None,
  addressLine2: Option[String] = None,
  addressState: Option[String] = None,
  addressZip: Option[String] = None,
  addressZipCheck: Option[String] = None,
  brand: Option[String] = None,
  country: Option[String]  = None,
  customer: Option[String]  = None,
  cvcCheck: Option[String] = None,
  dynamicLast4: Option[String] = None,
  expMonth: Int,
  expYear: Int,
  funding: String,
  last4: String,
  name: Option[String],
  tokenizationMethod: Option[String]

) 

object PaySourceModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PaySourceModel]

}

case class PaySourceCollectionModel(data: List[PaySourceModel])
object PaySourceCollectionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PaySourceCollectionModel]

}

case class PayChargeModel (
  id: String,
  `object`: String,
  amount: Int,
  amountRefunded: Option[Int],
  //applicationFee: Option[String],
  //balanceTransaction: Option[String],
  captured: Boolean,
  created: Long,
  currency: String,
  customer: Option[String],
  description: Option[String],
  destination: Option[String],
  dispute: Option[String],
  failureCode: Option[String],
  failureMessage: Option[String], 
  //fraud_details: {
  //},
  invoice: Option[String],
  //livemode: Boolean,
  //metadata": {
  //},
  paid: Boolean,
  receiptEmail: Option[String],
  receiptNumber: Option[String],
  refunded: Boolean,
  //refunds": {
  //  "object": "list",
  //  "data": [
  //  ],
  //  "has_more": false,
  //  "total_count": 0,
  //  "url": "/v1/charges/ch_176QbkDWowVoQgSoIa0CtmVy/refunds"
  //},
  //shipping: Option[String], 
  source: Option[PaySourceModel],
  statementDescriptor: Option[String],
  status: String
)


object PayChargeModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayChargeModel]
}


case class PayChargeCollectionModel(count: Int, data: List[PayChargeModel])
object PayChargeCollectionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayChargeCollectionModel]

}


case class PayCouponModel (
  id: String,
  amountOff: Int,
  created: Long,
  currency: String,
  duration: String,
  durationInMonths: Option[Int],
  livemode: Boolean,
  maxRedemptions: Option[Int],
  percentOff: Int,
  redeemBy: Option[Long],
  timesRedeemed: Option[Int],
  valid: Boolean
  ) 
  
object PayCouponModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayCouponModel]
} 

case class PayCouponCollectionModel(count: Int, data: List[PayCouponModel])
object PayCouponCollectionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayCouponCollectionModel]
}


case class PayDiscountModel(
  id: String,
  coupon: PayCouponModel,
  customer: String,
  start: Long,
  end: Option[Long],
  subscription: Option[String]  
) 
object PayDiscountModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayDiscountModel]
}


case class PayPlanModel (
  id: String,
  amount: Int,
  created: Long,
  currency: String,
  interval: String,
  intervalCount: Option[Int],
  livemode: Boolean,
  name: String,
  statementDescriptor: Option[String],
  trialPeriodDays: Option[Int])
  
object PayPlanModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayPlanModel]
}


case class PaySubscriptionModel (

  id: String,
  applicationFeePercent: Option[Long],
  cancelAtPeriodEnd: Option[Boolean],
  canceledAt: Option[Boolean],
  currentPeriodEnd: Option[Long],
  currentPeriodStart: Option[Long],
  customer: String,
  discount: Option[String],
  endedAt: Option[Long],
  plan: PayPlanModel,
  start: Long,
  status: String,
  taxPercent: Option[String],
  trialEnd: Option[Long],
  trialStart: Option[Long]

) 
  
object PaySubscriptionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PaySubscriptionModel]
}

case class PaySubscriptionCollectionModel(totalCount: Int, data: List[PaySubscriptionModel])
object PaySubscriptionCollectionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PaySubscriptionCollectionModel]

}

case class PayCustomerModel (
  id: String,
  accountBalance: Option[Int],
  created: Long,
  currency: String,
  defaultSource: Option[String],
  delinquent: Option[Boolean],
  description: Option[String],
  discount: Option[PayDiscountModel],
  email: Option[String],
  livemode: Boolean,
  sources: Option[PaySourceCollectionModel],
  subscriptions: Option[PaySubscriptionCollectionModel]
  )

object PayCustomerModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayCustomerModel]
}



case class DeletedCustomerModel(id: String, deleted: Boolean)

case class PayCustomerCollectionModel(count: Int, data: List[PayCustomerModel])


  
case class NextRecurringChargeModel(amount: Int, date: String)
object NextRecurringChargeModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[NextRecurringChargeModel]
}


  
case class PayInvoiceItemModel (
  id: String,
  amount: Int,
  currency: String,
  customer: Option[String] = None,
  date: Long,
  description: Option[String],
  discountable: Boolean,
  invoice: Option[String],
  livemode: Boolean,
  period: Option[String] = None,
  plan: Option[PayPlanModel],
  proration: Boolean,
  quantity: Option[Int],
  subscription: Option[String]
  ) 
  
object PayInvoiceItemModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayInvoiceItemModel]
}

case class DeletedInvoiceItemModel(id: String, deleted: Boolean)
object DeletedInvoiceItemModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[DeletedInvoiceItemModel]
}

case class PayInvoiceItemCollectionModel(count: Int, data: List[PayInvoiceItemModel])
object PayInvoiceItemCollectionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayInvoiceItemCollectionModel]
}


case class PayInvoiceLineSubscriptionPeriodModel(start: Long, end: Long)
object PayInvoiceLineSubscriptionPeriodModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayInvoiceLineSubscriptionPeriodModel]
}

case class PayInvoiceLineSubscriptionModel(plan: PayPlanModel, amount: Int, period: PayInvoiceLineSubscriptionPeriodModel)
object PayInvoiceLineSubscriptionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayInvoiceLineSubscriptionModel]
}

case class PayInvoiceLinesModel (
  subscriptions: List[PayInvoiceLineSubscriptionModel],
  invoiceItems: List[PayInvoiceItemModel],
  prorations: List[PayInvoiceItemModel]
  ) 
object PayInvoiceLinesModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayInvoiceLinesModel]
}
  
case class PayInvoiceModel  (
  id: Option[String],
  amountDue: Int,
  applicationFee: Option[Int] = None,
  //attemptCount: Int,
  attempted: Boolean,
  charge: Option[String],
  //closed: Boolean,
  customer: String,
  date: Long,
  //description: Option[String],
  discount: Option[PayDiscountModel],
  endingBalance: Option[Int],
  forgiven: Boolean,
  lines: PayInvoiceLinesModel,
  livemode: Boolean,
  nextPaymentAttempt: Option[Long],
  paid: Boolean,
  periodStart: Long,
  periodEnd: Long,
  startingBalance: Int,
  // statementDescriptor: Option[String],
  subscription: Option[String],
  //subscriptionProrationDate: Option[Int],
  subtotal: Int,
  tax: Option[Int],
  taxPercent: Option[Long],
  total: Int
  )

object PayInvoiceModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayInvoiceModel]
}
  
case class PayInvoiceCollectionModel(count: Int, data: List[PayInvoiceModel])
object PayInvoiceCollectionModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayInvoiceCollectionModel]
}


case class PayTokenModel(
  id: String,
  bankAccount: Option[String],
  card: PayCardModel,
  clientIp: String,
  created: Long,
  livemode: Boolean,
  `type`: String,
  used: Boolean
  ) 

object PayTokenModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[PayTokenModel]
}



case class DeletedCouponModel(id: String, deleted: Boolean)



case class AccountModel (
  id: String,
  email: Option[String],
  statementDescriptor: Option[String],
  detailsSubmitted: Boolean,
  chargeEnabled: Boolean,
  currenciesSupported: Array[String]
) 

object AccountModel {
	/** serialize/Deserialize into/from JSON value */
	implicit val format = Json.format[AccountModel]
} 



