/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
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
    var text = ""
    for(ndx <- 0 until items.size)
    {
      val item = items(ndx)
      if(ndx > 0) {
        text += delimiter
      }
      text += serializer(item)
    }
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
    valueOrDefault(opt.getOrElse(null), defaultVal)
  }


  def substring(text:String, pattern:String): Option[(String, String)] = {
    if(!isNullOrEmpty(text) && isNullOrEmpty(pattern)) {
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
    if(isNullOrEmpty(text))
      return Map[String,String]()
    val tokens = text.split(delimiter)
    val map = scala.collection.mutable.Map[String,String]()
    for(token <- tokens ){
      val finalToken = if(trim) token.trim else token
      map(finalToken) = finalToken
    }
    map.toMap
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


  def isMatch(text1:String, text2:String):Boolean =
  {
    if(text1 == null && text2 == null)
      true
    else if(text1 != null && text2 == null)
      false
    else if (text1 == null && text2 != null)
      false
    else
      text1 == text2
  }


  def compare(text1:String, text2:String, ignoreCase:Boolean = false):Int =
  {
    if(text1 == null && text2 == null)       0
    else if(text1 != null && text2 == null)  1
    else if(text1 == null && text2 != null)  0
    else if(ignoreCase) text2.compareToIgnoreCase(text2)
    else                text1.compareTo(text2)
  }


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
    if (items != null && items.nonEmpty)
      items.reduce( (a,b) => if (a.length > b.length ) a else b ).length
    else
      0
  }


  def pad(text:String, max:Int):String =
  {
    if (text.length == max)
      return text
    var pad = ""
    var count = 0
    while(count < max - text.length)
    {
      pad += " "
      count = count + 1
    }
    text + pad
  }


  def delimited(values:String*):String = {
    var buffer = ""
    var ndx = 0
    for(value <- values ){
      if(ndx > 0){
        buffer = buffer + ","
      }
      if(!Strings.isNullOrEmpty(value)){
        val formatted = value.replaceAllLiterally(",","")
        buffer = buffer + formatted
      }
      ndx = ndx + 1
    }
    buffer
  }


  def isInteger(input: String): Boolean = input.forall(_.isDigit)


  def isDouble(input: String): Boolean = {
    var totalDecimal = 0
    var ndx = 0
    var isDouble = true
    while(ndx < input.length && isDouble ){
      val ch = input(ndx)
      if (ch == '.'){
        if(totalDecimal == 0)
          totalDecimal += 1
        else if (totalDecimal > 0 )
          isDouble = false
      }
      else if( !ch.isDigit ) {
        isDouble = false
      }
      ndx += 1
    }
    isDouble
  }


  def toId(text:String, lowerCase:Boolean = true):String = {
    if(isNullOrEmpty(text)){
      return "_"
    }
    val formatted = text.trim.replaceAllLiterally(" ", "_")
    val finalText = if(lowerCase) formatted.toLowerCase else formatted
    finalText
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
