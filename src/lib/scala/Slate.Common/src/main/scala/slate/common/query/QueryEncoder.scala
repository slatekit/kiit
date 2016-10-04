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

package slate.common.query

import slate.common.{DateTime, Strings}

object QueryEncoder {


  def convertVal(value:Any): String =
  {
    if(value == null ) return ""

    var result = ""
    if (value.isInstanceOf[Boolean]) {
      val b = value.asInstanceOf[Boolean]
      result = if(b) "1" else "0"
    }
    else if (value.isInstanceOf[DateTime])
    {
      result = value.asInstanceOf[DateTime].toStringMySql()
    }
    else if (value.isInstanceOf[String])
    {
      val s = QueryEncoder.ensureValue(value.asInstanceOf[String])
      result = if(Strings.isNullOrEmpty(s)) "''" else "'" + s + "'"
    }
    else if (value.isInstanceOf[(String, Boolean)])
    {
      val ts = value.asInstanceOf[(String,Boolean)]._1
      val s = QueryEncoder.ensureValue(ts)
      result = if(Strings.isNullOrEmpty(ts)) "''" else "'" + s + "'"
    }
    else
    {
      result = value.toString
    }
    result
  }

  /**
   * ensures the text value supplied be escaping single quotes for sql.
   * @param text
   * @return
   */
  def ensureValue(text:String):String =
  {
    if (Strings.isNullOrEmpty(text))
      return text

    text.replace("'", "''")
  }


  def ensureField(text:String): String =
  {
    // Validate.
    if (Strings.isNullOrEmpty(text)) return Strings.empty

    // Get all lowercase without spaces.
    val trimmed = text.toLowerCase.trim()

    val buffer = new StringBuilder()
    var c:Char = ' '


    // Now go through each character.
    for (ndx <- 0 until trimmed.length())
    {
      c = trimmed(ndx)

      // Invalid char ? Go to next one.
      if (Character.isDigit(c) || Character.isLetter(c) || c == '_')
      {
        buffer.append(c)
      }
    }
    buffer.toString()
  }


  /**
   * ensures the comparison operator to be any of ( = > >= < <= != is), other wise
   * defaults to "="
   * @param compare
   * @return
   */
  def ensureCompare(compare:String) :String =
  {
    if ("=".equalsIgnoreCase(compare))
      return "="
    if (">".equalsIgnoreCase(compare))
      return ">"
    if (">=".equalsIgnoreCase(compare))
      return ">="
    if ("<".equalsIgnoreCase(compare))
      return "<"
    if ("<=".equalsIgnoreCase(compare))
      return "<="
    if ("!=".equalsIgnoreCase(compare))
      return "!="
    if ("is".equalsIgnoreCase(compare))
      return "is"
    "="
  }
}
