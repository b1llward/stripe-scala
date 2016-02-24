package com.vpayproc

import play.api.libs.json.{JsValue,JsObject}
import scala.concurrent.Future
import scala.util.Try
import play.api.libs.json._
import net.liftweb.json
import net.liftweb.json.JsonDSL._
import scala.util.{Failure, Success, Try}
/**
 * DAO for MongoDB documents.
 * 
 * @author      Bill Ward, Luong Ba Linh (luongbalinh)
 */

trait VWService {

  implicit val formats = json.DefaultFormats
  val classURL = ""
  def instanceURL(id: String): String
  //def request(method: String, command: String, params: Map[String,Seq[String]] = Map.empty): Future[json.JValue]
  //def request[T: Manifest](method: String, command: String, params: Map[String,Seq[String]] = Map.empty): Future[T]
  def request[T: Manifest](method: String, command: String, params: Map[String,Seq[String]] = Map.empty): Future[Try[T]]

}