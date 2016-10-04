/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.server.utils

import akka.http.scaladsl.server.RequestContext
import slate.common.Inputs
import spray.json._

class HttpInputs(private val _ctx:RequestContext, private val _json:Option[JsObject]) extends Inputs {

  private val _uri = _ctx.request.getUri()

  /// <summary>
  override def getString(key: String) : String =
  {
    // Check: Query string first
    val queryParam = _ctx.request.getUri().query().get(key)
    if(queryParam.isPresent){
      return queryParam.get()
    }

    // Check: Form data ( Json )
    if(_json.isDefined) {
      val json = _json.get
      if (json.fields.contains(key)) {
        val jsVal = json.fields(key)
        if (jsVal.isInstanceOf[JsString]) {
          val text = jsVal.asInstanceOf[JsString].value
          return text
        }
        val text = jsVal.asInstanceOf[JsValue].toString()
        return text
      }
    }
    // Not present
    ""
  }


  /// <summary>
  override def getStringOrElse(key: String, defaultVal:String) : String =
  {
    // Check: Query string first
    val queryParam = _ctx.request.getUri().query().get(key)
    if(queryParam.isPresent){
      return queryParam.get()
    }

    // Check: Form data ( Json )
    if(_json.isDefined) {
      val json = _json.get
      if (json.fields.contains(key)) {
        val jsVal = json.fields(key)
        if (!jsVal.isInstanceOf[JsString])
          throw new IllegalArgumentException("key does not map to a string value : " + key)

        val text = jsVal.asInstanceOf[JsString].value
        return text
      }
    }
    // Not present
    defaultVal
  }


  /**
    * Gets a value from the query string or JSON post data.
    * @param key
    * @return
    */
  override def getValue(key: String): AnyVal =
  {
    if ( !containsKey(key) )
      throw new IllegalArgumentException("key not found in arguments : " + key)

    if(_json.isDefined){
      return _json.get.fields(key).asInstanceOf[AnyVal]
    }
    throw new IllegalArgumentException("Value not available")
  }


  /**
    * gets an object from the JSON post data.
    * @param key
    * @return
    */
  override def getObject(key: String): AnyRef =
  {
    if ( !containsKey(key) ) return null

    val result = _json.get.fields(key).asInstanceOf[JsObject]
    new HttpInputs(_ctx, Option(result))
  }


  /**
    * Whether or not the key is present in the query string or in the post JSON post data.
    * @param key
    * @return
    */
  override def containsKey(key: String): Boolean =
  {
    if (_json.isDefined && _json.get.fields.contains(key))
      return true

    val queryParam = _ctx.request.getUri().query().get(key)
    if(queryParam.isPresent){
      return true
    }

    false
  }


  override def size(): Int = {
    _json.fold[Int](0)( json => json.fields.size )
  }
}