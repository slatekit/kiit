/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.results

import slate.common.ObjectBuilderJson

class ResultBase {

  def success: Boolean           = false
  def code   : Int               = 0
  def data   : Option[Any]       = None
  def msg    : Option[String]    = None
  def err    : Option[Exception] = None
  def ext    : Option[Any]       = None
  def tag    : Option[String]    = None



  def print():Unit = {
    println("success        : " + success            )
    println("message        : " + msg                )
    println("code           : " + code               )
    println("data           : " + data               )
    println("tag            : " + tag.getOrElse("")  )
  }


  def toJson():String = {
    // NOTE: Using the object builder here for
    // quick/simple serialization without 3rd party dependencies
    val json = new ObjectBuilderJson(true, "  ")
    json.begin()
    json.putString("success"  , if( success ) "true" else "false"     )
    json.putString("code"     , code.toString  )
    json.putString("data"     , data.map[String]( d => d.toString ).getOrElse("null"))
    json.putString("msg"      , msg.getOrElse("null"))
    json.putString("err"      , err.map[String]( e => e.getMessage ).getOrElse("null"))
    json.putString("ext"      , ext.getOrElse("null").toString )
    json.putString("tag"      , tag.getOrElse("null") )
    json.end()
    val text = json.toString()
    text
  }
}

