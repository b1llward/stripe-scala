package com.vpayproc
/*
by Anurag Goel and Bill Ward.

* 
*/

import javax.inject.{Inject,Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future,Await}
import scala.util.{Failure, Success, Try}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import scala.util.Properties

import play.api.mvc._
import play.api.libs.ws._

import net.liftweb.json
import net.liftweb.json.JsonDSL._

import play.api.mvc._
import play.api.libs.ws._


@Singleton
class PayProcImpl @Inject()(ws: WSClient) extends VWServiceImpl(ws) with PayProcService
{
  // CHARGES
  // ~~~~~~~
  override def chargeCreate(params: Map[String,Seq[String]]): Future[Try[PayChargeModel]] = {

      request[PayChargeModel]("POST", "charges", params)
  }
  
  // UnTested below here
  override def retrieveCharge(id: String): Future[Try[PayChargeModel]] = {
    request[PayChargeModel]("GET", "charges/%s".format(instanceURL(id)))
  }

  override def allCharge(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayChargeCollectionModel]] = {
    request[PayChargeCollectionModel]("GET", "charges", params)
  }

  override def refundCharge(charge: PayChargeModel): Future[Try[PayChargeModel]] = {
    request[PayChargeModel]("POST", "%s/refund".format(instanceURL(charge.id)))
    
  }

  // SUBSCRIPTIONS & CUSTOMERS 
  // ~~~~~~~~~~~~~~~~~~~~~~~~~
  
  override def customerCreate(params: Map[String,Seq[String]]): Future[Try[PayCustomerModel]] = {
      request[PayCustomerModel]("POST", "customers", params)
  }
  
 
  override def cancelSubscription(customerID: String, subscriptionID: String): Future[Try[PaySubscriptionModel]] = {
    request[PaySubscriptionModel]("DELETE", "customers/%s/subscriptions/%s".format(customerID,subscriptionID))
  }
 
  // UnTested below here
  override def updateCustomer(params: Map[String,Seq[String]]): Future[Try[PayCustomerModel]] = {
    request[PayCustomerModel]("POST", "customers", params)
  }

  override def deleteCustomer(id: String): Future[Try[DeletedCustomerModel]]  = {
    request[DeletedCustomerModel]("DELETE", "customers/%s".format(id))
  }

  override def updateSubscription(params:Map[String,Seq[String]]): Future[Try[PaySubscriptionModel]] = {
    request[PaySubscriptionModel]("POST", "subscriptions", params)
  }


  override def retrieveCustomer(id: String): Future[Try[PayCustomerModel]] = {
    request[PayCustomerModel]("GET", "customers/%s".format(id))
  }

  override def allCustomer(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayCustomerCollectionModel]] = {
    request[PayCustomerCollectionModel]("GET", "customers", params)
  }
 
 
  // PLANS
  // ~~~~~~
  // UnTested below here
  override def planUpdate(params: Map[String,Seq[String]]): Future[Try[PayPlanModel]] = {
    request[PayPlanModel]("POST", "plans", params)
  }

  override def planCreate(params: Map[String,Seq[String]]): Future[Try[PayPlanModel]] = {
    request[PayPlanModel]("POST", "plans", params)
  }

  override def planRetrieve(id: String): Future[Try[PayPlanModel]] = {
    request[PayPlanModel]("GET", "plans/%s".format(id))
  }
/*
  override def planAll(params: Map[String,_] = Map.empty): PlanCollection = {
    request("GET", classURL, params).extract[PlanCollection]
  }
 */
/*
  override def planDelete(): DeletedPlan = {
    request("DELETE", instanceURL(this.id)).extract[DeletedPlan]
  }
  */ 
 



  // INVOICE ITEM
  // ~~~~~~~~~~~~
  // UnTested below here

  override def updateInvoiceItem(id: String, params: Map[String,Seq[String]]): Future[Try[PayInvoiceItemModel]] = {
    request[PayInvoiceItemModel]("POST", "invoiceitems/%s".format(instanceURL(id)), params)
  }

  override def deleteInvoiceItem(id: String): Future[Try[DeletedInvoiceItemModel]] = {
    request[DeletedInvoiceItemModel]("DELETE", "invoiceitems/%s".format(instanceURL(id)))
  }
  
  override def createInvoiceItem(params: Map[String,Seq[String]]): Future[Try[PayInvoiceItemModel]] = {
    request[PayInvoiceItemModel]("POST", "invoiceitems", params)
  }

  override def retrieveInvoiceItem(id: String): Future[Try[PayInvoiceItemModel]] = {
    request[PayInvoiceItemModel]("GET", "invoiceitems/%s".format(instanceURL(id)))
  }

  override def allInvoiceItem(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayInvoiceItemCollectionModel]] = {
    request[PayInvoiceItemCollectionModel]("GET", "invoiceitems", params)
  }


  // INVOICE
  // ~~~~~~~
  // UnTested below here
  
  override def retrieveInvoice(id: String): Future[Try[PayInvoiceModel]] = {
    request[PayInvoiceModel]("GET", "invoices/%s".format(instanceURL(id)))
  }

  override def allInvoice(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayInvoiceCollectionModel]] = {
    request[PayInvoiceCollectionModel]("GET", "invoices", params)
  }

  override def upcomingInvoice(params: Map[String,Seq[String]]): Future[Try[PayInvoiceModel]] = {
    request[PayInvoiceModel]("GET", "invoices/upcoming", params)
  }


  // TOKENS
  // ~~~~~~~
  // UnTested below here
   
  override def createToken(params: Map[String,Seq[String]]): Future[Try[PayTokenModel]] = {
    request[PayTokenModel]("POST", "tokens", params)
  }

  override def retrieveToken(id: String): Future[Try[PayTokenModel]] = {
    request[PayTokenModel]("GET", "tokens/%s".format(instanceURL(id)))
  }

  
  // COUPONS
  // ~~~~~~~
  // UnTested below here
    
  override def deleteCoupon(id: String): Future[Try[DeletedCouponModel]] = {
    request[DeletedCouponModel]("DELETE", "coupons/%s".format(instanceURL(id)))
  }

  override def createCoupon(params: Map[String,Seq[String]]): Future[Try[PayCouponModel]] = {
    request[PayCouponModel]("POST", "coupons", params)
  }

  override def retrieveCoupon(id: String): Future[Try[PayCouponModel]] = {
    request[PayCouponModel]("GET", "coupons/%s".format(instanceURL(id)))
  }

  override def allCoupon(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayCouponCollectionModel]] = {
    request[PayCouponCollectionModel]("GET", "coupons", params)
  }

  
  // ACCOUNT
  // ~~~~~~~
  // UnTested below here
  
  override def retrieveAccount: Future[Try[AccountModel]] = {
    request[AccountModel]("GET", "account")
  }

  
  
  
}

