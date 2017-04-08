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

package slate.common

object Strings {

  /**
   * empty string
   */
  val empty = ""


  /**
   * used in reflection
   */
  val typeString = ""


  /**
   * shortcut for newline / lineseparator
 *
   * @return
   */
  def newline():String = System.lineSeparator


  /**
   * serializes an object into a json string
 *
   * @param obj
   * @return
   */
  def serialize(obj:Any):String = {
    obj match {
      case null             => "null"
      case Unit             => "null"
      case None             => "null"
      case s:Option[Any]    => serialize(s.getOrElse(None))
      case s:Result[Any]    => serialize(s.getOrElse(None))
      case s:String => toStringRep(s)
      case s:Int            => s.toString
      case s:Long           => s.toString
      case s:Double         => s.toString
      case s:Boolean        => s.toString.toLowerCase
      case s:DateTime       => "\"" + s.toString() + "\""
      case s:Seq[Any]       => "[ " + mkString[Any](s, serialize) + "]"
      case s: AnyRef        => { s.toString }
      case _                => obj.toString
    }
  }


  /**
    * make string implementation with a function callback to serialize an item
    *
    * @param items
    */
  def mkString[T](items:Seq[T], serializer: (T) => String, delimiter:String = ", "): String =
  {
    val buff = new StringBuilder()
    for(ndx <- 0 until items.size)
    {
      val item = items(ndx)
      if(ndx > 0) {
        buff.append( delimiter )
      }
      buff.append(serializer(item))
    }
    val text = buff.toString()
    text
  }


  def valueOrDefault(text:String, defaultVal:String): String =
  {
    text match {
      case null => defaultVal
      case ""   => defaultVal
      case _    => text
    }
  }


  def valueOptionOrDefault(opt:Option[String], defaultVal:String): String = {
    valueOrDefault(opt.getOrElse(defaultVal), defaultVal)
  }


  def substring(text:String, pattern:String): Option[(String, String)] = {
    if(!isNullOrEmpty(text) && !isNullOrEmpty(pattern)) {
      val ndxPattern = text.indexOf(pattern)
      if (ndxPattern < 0) {
        None
      }
      else {
        val part1 = text.substring(0, ndxPattern + pattern.length)
        val remainder = text.substring(ndxPattern + pattern.length)
        Some((part1, remainder))
      }
    }
    else
      None
  }


  def split(text:String, delimiter:Char):Array[String] =
  {
    if(isNullOrEmpty(text))
      Array[String]()
    else
      text.split(delimiter)
  }


  def splitToMap(text:String, delimiter:Char = ',', trim:Boolean = true):Map[String,String] =
  {
    // By default string
    splitToMapOfType(text, delimiter, trim, None, None).asInstanceOf[Map[String,String]]
  }


  def splitToMapOfType(text:String,
                       delimeterPairs:Char = ',',
                       trim:Boolean = true,
                       delimiterValue:Option[Char] = None,
                       keyConverter:Option[(String)=>Any] = None,
                       valConverter:Option[(String)=>Any] = None ):Map[_,_] =
  {
    if(isNullOrEmpty(text)) {
      Map[Any, Any]()
    }
    else {
      val pairs = text.split(delimeterPairs)
      val map = scala.collection.mutable.Map[Any, Any]()
      for (pair <- pairs) {
        val keyVal:(String,String) = delimiterValue.fold((pair,pair))( d => {
          val tokens = pair.split(d)
          (tokens(0), tokens(1))
        })
        val pkey = if(trim) keyVal._1.trim() else keyVal._1
        val pval = if(trim) keyVal._2.trim() else keyVal._2
        val finalKey = keyConverter.fold[Any](pkey)( k => k(pkey))
        val finalVal = valConverter.fold[Any](pval)( c => c(pval))
        map(finalKey) = finalVal
      }
      map.toMap
    }
  }


  def splitToMapWithPairs(text:String, delimiterPairs:Char = ',', delimiterKeyValue:Char = '=', trim:Boolean = true):Map[String,String] =
  {
    Option(text).fold(Map[String,String]())( t => {
      val map = scala.collection.mutable.Map[String,String]()
      val pairs = text.split(delimiterPairs)
      for(pair <- pairs ){
        val finalPair = if(trim) pair.trim else pair
        val tokens = finalPair.split(delimiterKeyValue)
        val key = if(trim) tokens(0).trim else tokens(0)
        val kval = if(trim) tokens(1).trim else tokens(1)
        map(key) = kval
      }
      map.toMap
    })
  }


  def isNullOrEmpty(text:String):Boolean = text == null || text == ""


  def isMatch(text1:String, text2:String):Boolean = text1 == text2


  def valueOrDefault(primary:String, secondary:String, defaultVal:String): String =
  {
    if(isNullOrEmpty(primary))
      primary
    else if(isNullOrEmpty(secondary))
      secondary
    else
      defaultVal
  }


  def maxLength(items:List[String]):Int =
  {
    val len = Option(items).fold(0)( all => all.size)
    len match {
      case 0 => 0
      case _ =>  items.reduce( (a,b) => if (a.length > b.length ) a else b ).length
    }
  }


  def pad(text:String, max:Int):String =
  {
    val len = if(isNullOrEmpty(text)) 0 else text.length
    if( len == 0  )
      text
    else if ( len == max)
      text
    else
      text + 0.until(max - text.length).foldLeft("")( (s, v) => s + " ")
  }


  def delimited(values:String*):String = values.mkString(",")


  def isInteger(input: String): Boolean = input.forall(_.isDigit)


  def isDouble(input: String): Boolean = {
    try {
      java.lang.Double.parseDouble(input)
      true
    } catch {
      case e: NumberFormatException => false
    }
  }


  def toId(text:String, lowerCase:Boolean = true):String = {
    if(isNullOrEmpty(text)){
      "_"
    }
    else {
      val formatted = text.trim.replaceAllLiterally(" ", "_")
      val finalText = if (lowerCase) formatted.toLowerCase else formatted
      finalText
    }
  }


  def toStringRep(text:String):String = {
    text match {
      case null  => "null"
      case ""    => "\"" + "\""
      case _     => "\"" + text.replaceAllLiterally("\\", "\\\\")
                    .replaceAllLiterally("\"", "\\\"") + "\""
    }
  }
}
