package com.vpayproc
import scala.concurrent.{Future,Await}
import scala.util.{Failure, Success, Try}

trait PayProcService extends VWService {
  def chargeCreate(params: Map[String,Seq[String]]):  Future[Try[PayChargeModel]] 
  def retrieveCharge(id: String): Future[Try[PayChargeModel]] 
  def allCharge(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayChargeCollectionModel]] 
  def refundCharge(charge: PayChargeModel): Future[Try[PayChargeModel]] 
  
  
  def customerCreate(params: Map[String,Seq[String]]): Future[Try[PayCustomerModel]]
  def cancelSubscription(customerID: String, subscriptionID: String): Future[Try[PaySubscriptionModel]]
  def updateCustomer(params: Map[String,Seq[String]]): Future[Try[PayCustomerModel]] 
  def deleteCustomer(id: String): Future[Try[DeletedCustomerModel]]
  def updateSubscription(params:Map[String,Seq[String]]): Future[Try[PaySubscriptionModel]]
  def retrieveCustomer(id: String): Future[Try[PayCustomerModel]] 
  def allCustomer(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayCustomerCollectionModel]]   
  
  
  def planUpdate(params: Map[String,Seq[String]]): Future[Try[PayPlanModel]] 
  def planCreate(params: Map[String,Seq[String]]): Future[Try[PayPlanModel]]
  def planRetrieve(id: String): Future[Try[PayPlanModel]]
  
  
  def updateInvoiceItem(id: String, params: Map[String,Seq[String]]): Future[Try[PayInvoiceItemModel]]
  def deleteInvoiceItem(id: String): Future[Try[DeletedInvoiceItemModel]] 
  def createInvoiceItem(params: Map[String,Seq[String]]): Future[Try[PayInvoiceItemModel]] 
  def retrieveInvoiceItem(id: String): Future[Try[PayInvoiceItemModel]] 
  def allInvoiceItem(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayInvoiceItemCollectionModel]] 
  
  def retrieveInvoice(id: String): Future[Try[PayInvoiceModel]] 
  def allInvoice(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayInvoiceCollectionModel]]
  def upcomingInvoice(params: Map[String,Seq[String]]): Future[Try[PayInvoiceModel]] 
  
  
  def createToken(params: Map[String,Seq[String]]): Future[Try[PayTokenModel]]
  def retrieveToken(id: String): Future[Try[PayTokenModel]] 
    
  def deleteCoupon(id: String): Future[Try[DeletedCouponModel]] 
  def createCoupon(params: Map[String,Seq[String]]): Future[Try[PayCouponModel]] 
  def retrieveCoupon(id: String): Future[Try[PayCouponModel]] 
  def allCoupon(params: Map[String,Seq[String]] = Map.empty): Future[Try[PayCouponCollectionModel]] 
  
  def retrieveAccount: Future[Try[AccountModel]] 
}
  
