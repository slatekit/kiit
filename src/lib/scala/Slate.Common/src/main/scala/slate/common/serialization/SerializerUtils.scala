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
    val buff = new StringBuilder("<table>")
    for(item <- items)
    {
      for ((k,v) <- item)
      {
        buff.append("<tr>")
        buff.append("<td>" + k + "</td><td>" + ( if ( Option(v).isEmpty ) "" else v.toString) + "</td>")
        buff.append("<tr>")
      }
    }

    buff.append("</table>")
    val html = buff.toString()
    html
  }


  /**
   * converts the list of tuples ( string, object ) into a html table
   * @param items
   * @return
   */
  def asHtmlTable(items:List[(String,Any)]): String =
  {
    val buff = new StringBuilder("<table>")
    for(item <- items)
    {
      buff.append("<tr>")
      val value = if ( Option(item._2).isEmpty ) "" else item._2.toString
      buff.append("<td>" + item._1 + "</td><td>" + value + "</td>")
      buff.append("</tr>")
    }

    buff.append("</table>")
    val html = buff.toString()
    html
  }


  /**
   * converts the list of tuples ( string, object ) into a json object.
   * @param items
   * @return
   */
  def asJson(items:List[(String,Any)]): String =
  {
    val buff = new StringBuilder("{")
    for(i <- items.indices)
    {
      if( i > 0 ) buff.append(",")

      val item = items(i)
      buff.append("\"" + item._1 + "\":")
      val value = if ( Option(item._2).isEmpty  ) "" else item._2.toString
      buff.append("\"" + escapeJson(value) + "\"")
    }

    buff.append("}")
    val json = buff.toString()
    json
  }


  private def escapeJson(text:String) : String = {
    text.replaceAllLiterally("\"", "\\\"").replaceAllLiterally("\\", "\\\\")
  }
}

