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
import slate.common.{Strings, Ensure, Inputs}

/**
  * abstraction layer over the akka http headers to support Protocol Independent APIs.
  * @param _ctx
  */
class HttpHeaders(private val _ctx:RequestContext) extends Inputs {


  /**
    * gets a string from the http headers
    * @param key
    * @return
    */
  override def getString(key: String) : String =
  {
    val header = _ctx.request.getHeader(key)
    if(header.isPresent){
      return header.get.value
    }
    ""
  }


  /**
    * gets a string from the http headers if present or returns the default value
    * @param key
    * @param defaultVal
    * @return
    */
  override def getStringOrElse(key: String, defaultVal:String) : String =
  {
    val header = _ctx.request.getHeader(key)
    if(header.isPresent){
      return header.get.value
    }
    defaultVal
  }


  /**
    * Gets a value from the header
    *
    * @param key
    * @return
    */
  override def getValue(key: String): AnyVal =
  {
    val text = getStringOrElse(key, "").toLowerCase
    if(text == "true") return true
    if(text == "false") return false
    if(Strings.isInteger(text)) return text.toInt
    if(Strings.isDouble(text)) return text.toDouble
    if(text.length == 1 && text(0).isLetterOrDigit) return text(0)
    throw new IllegalArgumentException(s"key ${key} is not a value")
  }


  /**
    * gets an object from the header data.
    *
    * @param key
    * @return
    */
  override def getObject(key: String): AnyRef =
  {
    val header = _ctx.request.getHeader(key)
    if(header.isPresent){
      return header.get.value
    }
    ""
  }


  /**
    * Whether or not the key is present in the query string or in the post JSON post data.
    *
    * @param key
    * @return
    */
  override def containsKey(key: String): Boolean =
  {
    _ctx.request.getHeader(key).isPresent
  }


  override def size(): Int = {
    _ctx.request.headers.size
  }
}
