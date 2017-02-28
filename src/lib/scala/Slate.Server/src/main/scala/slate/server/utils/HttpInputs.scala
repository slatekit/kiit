/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.server.utils

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.server.RequestContext
import slate.common.{Inputs}
import slate.common.Funcs.{getStringByOrder, getStringByOrderOrElse}
import spray.json._


/**
  * Abstracts the inputs of a web request by handling input params coming from either the
  * 1. query string if GET
  * 2. post body ( if present ) with fallback to query string
  *
  * @param _ctx
  * @param _json
  */
class HttpInputs(private val _ctx:RequestContext,
                 private val _json:Option[JsObject]) extends Inputs {

  private val _uri         = _ctx.request.getUri()
  private val _queryParams = _uri.query().toList


  def isGet       : Boolean = _ctx.request.method == HttpMethods.GET
  def isPost      : Boolean = _ctx.request.method == HttpMethods.POST
  def isPut       : Boolean = _ctx.request.method == HttpMethods.PUT
  def isDelete    : Boolean = _ctx.request.method == HttpMethods.DELETE
  def hasBody     : Boolean = isPost | isPut | isDelete


  /**
    * gets the size of inputs which includes number of query params if get, otherwise
    * number of query params + number of post body json fields.
    *
    * @return
    */
  override def size(): Int = {
    val size =  _queryParams.size()
    val finalSize = if( isGet ) size else _json.fold[Int](size) ( json => size + json.fields.size )
    finalSize
  }


  /**
    * gets a string value from the query string if get method, otherwise,
    * gets the value from the post body if present in there or gets from query string
    *
    * @param key
    * @return
    */
  override def getString(key: String) : String = {
    if ( isGet )
      fromQueryString(key).getOrElse("")
    else
      getStringByOrder(key, fromPostBody, fromQueryString)
  }



  /**
    * gets a string value from the query string if get method or defaults, otherwise,
    * gets the value from the post body if present in there or gets from query string
    * or defaults
    *
    * @param key
    * @return
    */
  override def getStringOrElse(key: String, defaultVal:String) : String =
  {
    if ( isGet ) {
      fromQueryString(key).getOrElse(defaultVal)
    }
    else {
      getStringByOrderOrElse(key, fromPostBody, fromQueryString, defaultVal)
    }
  }


  /**
    * Gets a value from the query string or JSON post data.
 *
    * @param key
    * @return
    */
  override def getValue(key: String): AnyVal =
  {
    // TODO: Refactor this code to remove runtime exception
    // involves changing Inputs class in common.
    require(contains(key), "key not found in arguments : " + key)

    if ( _json.isDefined && _json.get.fields.contains(key)) {
       _json.get.fields(key).asInstanceOf[AnyVal]
    }
    else
      throw new IllegalArgumentException("Value not available")
  }


  /**
    * gets an object from the JSON post data.
 *
    * @param key
    * @return
    */
  override def getObject(key: String): AnyRef =
  {
    // NOTE: Need to make a minor change to the Inputs trait/class
    // to support avoid null
    if ( !containsKey(key) )
      null
    else {
      val result = _json.get.fields(key).asInstanceOf[JsObject]
      new HttpInputs(_ctx, Option(result))
    }
  }


  /**
    * Whether or not the key is present in the query string or in the post JSON post data.
    *
    * @param key
    * @return
    */
  override def containsKey(key: String): Boolean =
  {
    _uri.query.get(key).isPresent || (_json.isDefined && _json.get.fields.contains(key))
  }


  private def fromQueryString(key:String):Option[String] = {
    val queryParam = _uri.query().get(key)
    val value = if(queryParam.isPresent) Option(queryParam.get) else None
    value
  }


  private def fromPostBody(key:String):Option[String] = {
    val jsonVal = _json.fold[Option[String]](None)( json => {
      val result = if (json.fields.contains(key)) {
        val jsVal = json.fields(key)
        if (jsVal.isInstanceOf[JsString]) {
          val text = jsVal.asInstanceOf[JsString].value
          Option(text)
        }
        else {
          val text = jsVal.asInstanceOf[JsValue].toString()
          Option(text)
        }
      }
      else
        None
      result
    })
    jsonVal
  }
}