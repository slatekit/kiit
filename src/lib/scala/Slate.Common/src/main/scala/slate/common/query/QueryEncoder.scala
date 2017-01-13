/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.query

import slate.common.{DateTime, Strings}

object QueryEncoder {


  def convertVal(value:Any): String =
  {
    value match {
      case null               => ""
      case None               => "null"
      case s:Option[Any]      => convertVal(s.orNull)
      case s:String           => toString(s)
      case s:Int              => s.toString
      case s:Long             => s.toString
      case s:Double           => s.toString
      case s:Boolean          => if(s) "1" else "0"
      case s:DateTime         => "'" + s.toStringMySql + "'"
      case _                  => value.toString
    }
  }


  /**
   * ensures the text value supplied be escaping single quotes for sql.
    *
    * @param text
   * @return
   */
  def ensureValue(text:String):String =
  {
    text match {
      case null  => text
      case ""    => text
      case _     => text.replace("'", "''")
    }
  }


  def ensureField(text:String): String =
  {
    text match {
      case null  => ""
      case ""    => ""
      case _     => text.toLowerCase.trim().filter( c => c.isDigit || c.isLetter || c == '_')
                     .foldLeft[String]("")( (a,b) => a.toString + b.toString)
    }
  }


  /**
   * ensures the comparison operator to be any of ( = > >= < <= != is), other wise
   * defaults to "="
    *
    * @param compare
   * @return
   */
  def ensureCompare(compare:String) :String =
  {
    compare match {
      case "="  =>  "="
      case ">"  =>  ">"
      case ">=" => ">="
      case "<"  =>  "<"
      case "<=" => "<="
      case "!=" => "!="
      case "is" => "is"
      case _    => "="
    }
  }


  def toString(value:String): String ={
    val s = QueryEncoder.ensureValue(value.asInstanceOf[String])
    val res = if(Strings.isNullOrEmpty(s)) "''" else "'" + s + "'"
    res
  }
}
