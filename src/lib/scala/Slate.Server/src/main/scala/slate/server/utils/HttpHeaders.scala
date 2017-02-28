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

import akka.http.scaladsl.server.RequestContext
import slate.common.{Strings, Inputs}

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
    if(header.isPresent) header.get.value else ""
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
    if(header.isPresent) header.get.value else defaultVal
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
    val result =
      if(text == "true")
        true
      else if(text == "false")
        false
      else if(Strings.isInteger(text))
        text.toInt
      else if(Strings.isDouble(text))
        text.toDouble
      else if(text.length == 1 && text(0).isLetterOrDigit)
        text(0)
      else
        throw new IllegalArgumentException(s"key ${key} is not a value")
    result
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
    if(header.isPresent) header.get.value else ""
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
