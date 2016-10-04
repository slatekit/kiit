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

package slate.common.serialization

import slate.common.Reflector

object SerializerUtils {


  /**
   * converts the object supplied, into an html table of properties
   * @param cc
   * @return
   */
  def asHtmlTable(cc:AnyRef): String =
  {
    val info = Reflector.getFields(cc)
    asHtmlTable(info)
  }


  /**
   * converts the map supplied into an html table of key/value pairs.
   * @param items
   * @return
   */
  def asHtmlTable(items:Map[String,Any]*): String =
  {
    var text = "<table>"
    for(item <- items)
    {
      for ((k,v) <- item)
      {
        text = text + "<tr>"
        text = text + "<td>" + k + "</td><td>" + ( if ( v == null ) "" else v.toString) + "</td>"
        text = text + "<tr>"
      }
    }

    text = text + "</table>"
    text
  }


  /**
   * converts the list of tuples ( string, object ) into a html table
   * @param items
   * @return
   */
  def asHtmlTable(items:List[(String,Any)]): String =
  {
    var text = "<table>"
    for(item <- items)
    {
      text = text + "<tr>"
      val value = if ( item._2 == null ) "" else item._2.toString
      text = text + "<td>" + item._1 + "</td><td>" + value + "</td>"
      text = text + "</tr>"
    }

    text = text + "</table>"
    text
  }


  /**
   * converts the list of tuples ( string, object ) into a json object.
   * @param items
   * @return
   */
  def asJson(items:List[(String,Any)]): String =
  {
    var text = "{"
    for(i <- items.indices)
    {
      if( i > 0 ) text = text + ","

      val item = items(i)
      text = text + "\"" + item._1 + "\":"
      val value = if ( item._2 == null ) "" else item._2.toString
      text = text + "\"" + escapeJson(value) + "\""
    }

    text = text + "}"
    text
  }


  private def escapeJson(text:String) : String = {
    text.replaceAllLiterally("\"", "\\\"").replaceAllLiterally("\\", "\\\\")
  }
}

