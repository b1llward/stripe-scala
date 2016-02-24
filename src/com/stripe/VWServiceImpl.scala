package com.vpayproc
/*
by Anurag Goel and Bill Ward
* 
*/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future,Await}
import scala.concurrent.duration.Duration

import java.net.URLEncoder
import javax.inject.{Inject,Singleton}

import scala.collection.JavaConversions._
//import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import scala.util.Properties
import scala.util.{Failure, Success, Try}

import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json.Json
import net.liftweb.json
import net.liftweb.json.JsonDSL._

import com.voticker.core.utils.Logging


sealed abstract class VWSException(msg: String, cause: Throwable = null) extends Exception(msg, cause)
case class VAPIException(msg: String, cause: Throwable = null) extends VWSException(msg, cause)
case class VAPIConnectionException(msg: String, cause: Throwable = null) extends VWSException(msg, cause)
case class VCardException(msg: String, code: Option[String] = None, param: Option[String] = None) extends VWSException(msg)
case class VInvalidRequestException(msg: String, param: Option[String] = None) extends VWSException(msg)
case class VAuthenticationException(msg: String) extends VWSException(msg)
case class VParseException(msg: String) extends VWSException(msg)


abstract class VWServiceImpl  @Inject()(ws: WSClient)  extends VWService with Logging {
  val ApiBase = "https://api.stripe.com/v1"
  val BindingsVersion = "1.1.2"
  val CharSet = "UTF-8"

  val apiKey: String = play.Play.application.configuration.getString("stripe.api_key")
  
  //lift-json format initialization
  //implicit val formats = json.DefaultFormats
  val singleInstanceURL = "%s/%s".format(ApiBase, className)
  val className = this.getClass.getSimpleName.toLowerCase.replace("$","")
  
  override val classURL = "%s/%ss".format(ApiBase, className)
  override def instanceURL(id: String): String = "%s/%s".format(classURL, id)


   /*
    val httpParams = new SyncBasicHttpParams().
      setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders).
      setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,CharSet).
      setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000). //30 seconds
      setParameter(CoreConnectionPNames.SO_TIMEOUT,80000) //80 seconds

    HttpClientBuilder.create().build();
    //new DefaultHttpClient(connectionManager, httpParams)
     * 
     */

  
  override def request[T: Manifest](method: String, command: String, params: Map[String,Seq[String]] = Map.empty): Future[Try[T]] = {

    
    val url = "%s/%s".format(ApiBase, command)
    log.debug(s"key=$apiKey")
    if (apiKey == null || apiKey.isEmpty) {
      Future(Failure(VAuthenticationException("No API key provided. See https://stripe.com/api for details or email support@stripe.com if you have questions.")))
    } else {
    
    //debug headers
    val javaPropNames = List("os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor")
    val javaPropMap = javaPropNames.map(n => (n.toString, Properties.propOrEmpty(n).toString)).toMap
    val fullPropMap = javaPropMap + (
      "scala.version" -> Properties.scalaPropOrEmpty("version.number"),
      "bindings.version" -> BindingsVersion,
      "lang" -> "scala",
      "publisher" -> "stripe"
      )
    log.debug(s"fullPropMap=$fullPropMap")      
    val client = ws.url(url)
                   //.withHeaders("X-Stripe-Client-User-Agent" -> json.compact(json.render(fullPropMap)))
                   //.withHeaders("User-Agent" -> "Stripe/v1 ScalaBindings/%s".format(BindingsVersion))
                   .withHeaders("Authorization"-> "Bearer %s".format(apiKey))
                   //.withHeaders("Idempotency-Key" -> "P8C2aVVBo3pqfa06")
                   //.withRequestTimeout(30000)

    log.debug(s"client=$client")
    val clientWithParams = params.foldLeft(client) { case (req, (k, v)) =>
        req.withQueryString(k -> v.toString())
    }
    //log.debug(s"clientWithParams=$clientWithParams")
    
/*
    val newparamList = Map("source" -> Seq("tok_176NB4DWowVoQgSol1fcXN6W")
                          ,"amount" -> Seq("444")
                          ,"currency" -> Seq("usd")
        )
    log.debug(s"newparamList=$newparamList")    
    * 
    */
    try {
      val request = method.toLowerCase match {
        case "get" => clientWithParams.get()
        case "delete" => clientWithParams.delete()
        case "post" => client.post(params)
        case _ => throw new VAPIConnectionException("Unrecognized HTTP method %r. This may indicate a bug in the Stripe bindings. Please contact support@stripe.com for assistance.".format(method))
      }
      
      log.debug("executed!")
      
      
      val futureResult = request.map {
        response => {
          val body = response.json.toString()
          log.debug(s"response body=$body")
          val iResp = interpretResponse(body, response.status)
          iResp match {
            case Success(resp) =>  {
              try {
                Success(resp.extract[T])
              } catch {
                case e => Failure(VParseException(e.getMessage))
              }
            }
            case Failure(e) => Failure(e)
          }
        }
      }
      
      futureResult
    } catch {
      case e @ (_: java.io.IOException ) => Future(Failure(VAPIConnectionException("Could not connect to Stripe (%s). Please check your internet connection and try again. If this problem persists, you should check Stripe's service status at https://twitter.com/stripe, or let us know at support@stripe.com.".format(ApiBase), e)))
    }
  }
  }
  

/*

  def deleteRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpDeleteWithBody(url)
    val deleteParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(deleteParamList), CharSet))
    request
  }

  def postRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpPost(url)
    val postParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(postParamList), CharSet))
    request
  }

*/

    //utility methods
  //def base64(in: String) = new String(Base64.encodeBase64(in.getBytes(CharSet)))
  def urlEncodePair(k:String, v: String) = "%s=%s".format(URLEncoder.encode(k, CharSet), URLEncoder.encode(v, CharSet))

  /*
      We want POST vars of form:
      {'foo': 'bar', 'nested': {'a': 'b', 'c': 'd'}}
      to become:
      foo=bar&nested[a]=b&nested[c]=d
  */
  def flattenParam(k: String, v: Any): List[(String, String)] = {
    v match {
      case None => Nil
      case m: Map[_,_] => m.flatMap(kv => flattenParam("%s[%s]".format(k,kv._1), kv._2)).toList
      case _ => List((k,v.toString))
    }
  }



  val CamelCaseRegex = new Regex("(_.)")

  def interpretResponse(rBody: String, rCode: Int): Try[json.JValue] = {
    val jsonAST = json.parse(rBody).transform {
      //converts json camel_case field names to Scala camelCase field names
      case json.JField(fieldName, x) => json.JField(CamelCaseRegex.replaceAllIn(
        fieldName, (m: Regex.Match) => m.matched.substring(1).toUpperCase), x)
    }
    if (rCode < 200 || rCode >= 300)
      handleAPIError(rBody, rCode, jsonAST)
    else 
      Success(jsonAST)
      
  }

  def handleAPIError(rBody: String, rCode: Int, jsonAST: json.JValue): Try[json.JValue] = {
    try {
      val error = jsonAST.extract[ErrorContainer].error
      rCode match {
        case (400 | 404) => Failure(VInvalidRequestException(error.message, param=error.param))
        case 401 => Failure(VAuthenticationException(error.message))
        case 402 => Failure(VCardException(error.message, code=error.code, param=error.param))
        case _ => Failure(VAPIException(error.message, null))
      }       
    } catch {
      case e: json.MappingException => Failure(VAPIException(
        "Unable to parse response body from API: %s (HTTP response code was %s)".format(rBody, rCode), e))
    }
    

  }
}


  
