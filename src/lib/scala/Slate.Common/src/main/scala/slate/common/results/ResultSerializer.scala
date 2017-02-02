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
package slate.common.results

import slate.common.{DateTime, Strings, Result}
import slate.common.serialization.{SerializerJson, ObjectBuilderJson}

class ResultSerializer {

  def toJson[T](res:Result[T]): String = {

    // NOTE: Using the object builder here for
    // quick/simple serialization without 3rd party dependencies
    val json = new ObjectBuilderJson(true, "  ")
    json.begin()
    json.putBoolean   ("success"  , res.success    )
    json.putInt       ("code"     , res.code  )
    json.putStringRaw ("value"    , serialize(res.getOrElse(None)))
    json.putString    ("msg"      , res.msg.getOrElse(""))
    json.putString    ("err"      , res.err.map[String]( e => e.getMessage ).getOrElse(""))
    json.putString    ("ext"      , res.ext.getOrElse("").toString )
    json.putString    ("tag"      , res.tag.getOrElse("") )
    json.end()
    val text = json.toString()
    text
  }


  def serialize(obj:Any):String = {
    obj match {
      case null             => "null"
      case Unit             => "null"
      case None             => "null"
      case s:Option[Any]    => serialize(s.getOrElse(None))
      case s:Result[Any]    => serialize(s.getOrElse(None))
      case s:String         => Strings.toStringRep(s)
      case s:Int            => s.toString
      case s:Long           => s.toString
      case s:Double         => s.toString
      case s:Boolean        => s.toString.toLowerCase
      case s:DateTime       => "\"" + s.toString() + "\""
      case s:Seq[Any]       => "[ " + serializeList(s) + "]"
      case s: AnyRef        => { val ser = new SerializerJson(); ser.serialize(s); }
      case _                => obj.toString
    }
  }


  /**
    * prints a list ( recursive
    *
    * @param items
    */
  def serializeList(items:Seq[Any]): String =
  {
    // NOTE: For this serialization, this approach
    // is faster than a more functional zipWithIndex, indices, etc.
    // especially if the lists are big.
    val buff = new StringBuilder()
    for(ndx <- 0 until items.size)
    {
      val item = items(ndx)
      if(ndx > 0) {
        buff.append(", ")
      }
      val s = serialize(item)
      buff.append(s)
    }
    val json = buff.toString()
    json
  }
}
