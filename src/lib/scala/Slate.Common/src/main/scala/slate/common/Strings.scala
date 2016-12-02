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

  val empty = ""


  val typeString = ""


  def newline():String = System.lineSeparator


  def serialize(obj:Any):String = {
    obj match {
      case null             => "null"
      case Unit             => "null"
      case None             => "null"
      case s:Option[Any]    => serialize(s.getOrElse(None))
      case s:Result[Any]    => serialize(s.getOrElse(None))
      case s:String         => Strings.stringRepresentation(s)
      case s:Int            => s.toString
      case s:Long           => s.toString
      case s:Double         => s.toString
      case s:Boolean        => s.toString.toLowerCase
      case s:DateTime       => "\"" + s.toString() + "\""
      case s:Seq[Any]       => "[ " + serializeList(s, serialize) + "]"
      case s: AnyRef        => { s.toString }
      case _                => obj.toString
    }
  }


  /**
   * prints a list ( recursive
   *
   * @param items
   */
  def serializeList(items:Seq[Any], serializer: (String) => String): String =
  {
    var text = ""
    for(ndx <- 0 until items.size)
    {
      val item = items(ndx)
      if(ndx > 0) {
        text += ", "
      }
      text += serialize(item)
    }
    text
  }



  def substring(text:String, pattern:String): Option[(String, String)] = {
    if(isNullOrEmpty(text) || isNullOrEmpty(pattern)){
      return None
    }
    val ndxPattern = text.indexOf(pattern)
    if (ndxPattern < 0 ) {
      return None
    }
    val part1 = text.substring(0, ndxPattern + pattern.length)
    val remainder = text.substring(ndxPattern + pattern.length)
    Some((part1, remainder))
  }


  def split(text:String, delimiter:Char):Array[String] =
  {
    if(isNullOrEmpty(text))
      return Array[String]()
    val tokens = text.split(delimiter)
    tokens
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


  def isNullOrEmpty(text:String):Boolean =
  {
    if(text == null) return true
    if(text == "") return true
    false
  }


  def isMatch(text1:String, text2:String):Boolean =
  {
    if(text1 == null && text2 == null)
      return true
    if(text1 != null && text2 == null)
      return false
    if(text1 == null && text2 != null)
      return false
    text1 == text2
  }


  def compare(text1:String, text2:String, ignoreCase:Boolean = false):Int =
  {
    if(text1 == null && text2 == null)
      return 0
    if(text1 != null && text2 == null)
      return 1
    if(text1 == null && text2 != null)
      return 0

    if(ignoreCase)
      return text2.compareToIgnoreCase(text2)

    text1.compareTo(text2)
  }


  def valueOrDefault(text:String, defaultVal:String): String =
  {
    if(isNullOrEmpty(text))
      return defaultVal
    text
  }


  def valueOrDefault(opt:Option[String], text:String): String = {
    if(!opt.isDefined)
      return text
    val primary = opt.get
    if(!Strings.isNullOrEmpty(primary))
      return primary

    text
  }


  def valueOrDefault(primary:String, secondary:String, defaultVal:String): String =
  {
    if(isNullOrEmpty(primary))
      return primary
    if(isNullOrEmpty(secondary))
      return secondary
    defaultVal
  }


  def maxLength(items:List[String]):Int =
  {
    if (items == null || items.isEmpty)
      return 0
    var maxLength = 1
    for (item <- items)
    {
      if (item.length > maxLength)
        maxLength = item.length
    }
    maxLength
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


   def stringRepresentation(text:String):String = {
    if (text == null) {
      return "null"
    }
    else if (Strings.isNullOrEmpty(text)) {
      return "\"" + "\""
    }
    "\"" + text.replaceAllLiterally("\\", "\\\\").replaceAllLiterally("\"", "\\\"") + "\""
  }
}
