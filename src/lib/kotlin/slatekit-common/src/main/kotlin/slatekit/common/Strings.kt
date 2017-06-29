/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common

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
    fun newline(): String = System.lineSeparator()


    fun valueOrDefault(text: String?, defaultVal: String): String {
        return text?.let { t -> if (t.isNullOrEmpty()) defaultVal else t } ?: defaultVal
    }

    val String.toCharMap: Map<Char, Boolean> get() = this.toCharArray().map { c -> c to true }.toMap()


    fun nonEmptyOrDefault(text: String, defaultVal: String): String =
            if (text.isBlank() || text.isEmpty())
                defaultVal
            else
                text


    fun split(text: String, ch: Char): List<String> {
        return text.split(ch)
    }


    fun substring(text: String, pattern: String): Pair<String, String>? {
        return if (!text.isNullOrEmpty() && !pattern.isNullOrEmpty()) {
            val ndxPattern = text.indexOf(pattern)
            if (ndxPattern < 0) {
                null
            }
            else {
                val part1 = text.substring(0, ndxPattern + pattern.length)
                val remainder = text.substring(ndxPattern + pattern.length)
                Pair(part1, remainder)
            }
        }
        else
            null
    }


    fun pad(text: String, max: Int): String {
        val len = if (text.isNullOrEmpty()) 0 else text.length
        val res = if (len == 0)
            text
        else if (len == max)
            text
        else {
            val remainder = " ".repeat(Math.abs(max - text.length))
            text + remainder
        }
        return res
    }


    fun splitToMapWithPairs(text: String, delimiterPairs: Char = ',', delimiterKeyValue: Char = '=', trim: Boolean = true): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val pairs = text.split(delimiterPairs)
        for (pair in pairs) {
            val finalPair = if (trim) pair.trim() else pair
            val tokens = finalPair.split(delimiterKeyValue)
            val key = if (trim) tokens[0].trim() else tokens[0]

            val kval = if (tokens.size > 1) {
                if (trim) tokens[1].trim() else tokens[1]
            }
            else {
                key
            }
            map[key] = kval

        }
        return map.toMap()
    }


    fun toId(text: String, lowerCase: Boolean = true): String {
        return if (text.isNullOrEmpty()) {
            "_"
        }
        else {
            val formatted = text.trim().replace(" ", "_")
            val finalText = if (lowerCase) formatted.toLowerCase() else formatted
            finalText
        }
    }


    fun splitToMapOfType(text: String,
                         delimeterPairs: Char = ',',
                         trim: Boolean = true,
                         delimiterValue: Char? = null,
                         keyConverter: ((String) -> Any)? = null,
                         valConverter: ((String) -> Any)? = null): Map<*, *> {
        return if (text.isNullOrEmpty()) {
            mapOf<Any, Any>()
        }
        else {
            val pairs = text.split(delimeterPairs)
            val map = mutableMapOf<Any, Any>()
            for (pair in pairs) {
                val keyVal: Pair<String, String> = delimiterValue?.let { d ->
                    val tokens = pair.split(d)
                    Pair(tokens[0], tokens[1])
                } ?: Pair(pair, pair)

                val pkey = if (trim) keyVal.first.trim() else keyVal.first
                val pval = if (trim) keyVal.second.trim() else keyVal.second
                val finalKey = keyConverter?.let { k -> k(pkey) } ?: pkey
                val finalVal = valConverter?.let { c -> c(pval) } ?: pval
                map.put(finalKey, finalVal)
            }
            map.toMap()
        }
    }

    /*
    fun serialize(obj:Any):String = {
      when obj {
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


    fun mkString[T](items:Seq[T], serializer: (T) => String, delimiter:String = ", "): String =
    {
      val buff = StringBuilder()
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


    fun valueOptionOrDefault(opt:Option[String], defaultVal:String): String = {
      valueOrDefault(opt.getOrElse(defaultVal), defaultVal)
    }


    fun split(text:String, delimiter:Char):Array[String] =
    {
      if(isNullOrEmpty(text))
        Array[String]()
      else
        text.split(delimiter)
    }


    fun splitToMap(text:String, delimiter:Char = ',', trim:Boolean = true):Map[String,String] =
    {
      // By default string
      splitToMapOfType(text, delimiter, trim, None, None).asInstanceOf[Map[String,String]]
    }





    fun isNullOrEmpty(text:String):Boolean = text == null || text == ""


    fun isMatch(text1:String, text2:String):Boolean = text1 == text2


    fun valueOrDefault(primary:String, secondary:String, defaultVal:String): String =
    {
      if(isNullOrEmpty(primary))
        primary
      else if(isNullOrEmpty(secondary))
        secondary
      else
        defaultVal
    }


    fun maxLength(items:List[String]):Int =
    {
      val len = Option(items).fold(0)( all => all.size)
      len match {
        case 0 => 0
        case _ =>  items.reduce( (a,b) => if (a.length > b.length ) a else b ).length
      }
    }


    fun delimited(values:String*):String = values.mkString(",")


    fun isInteger(input: String): Boolean = input.forall(_.isDigit)


    fun isDouble(input: String): Boolean = {
      try {
        java.lang.Double.parseDouble(input)
        true
      } catch {
        case e: NumberFormatException => false
      }
    }


    fun toStringRep(text:String):String = {
      text match {
        case null  => "null"
        case ""    => "\"" + "\""
        case _     => "\"" + text.replaceAllLiterally("\\", "\\\\")
                      .replaceAllLiterally("\"", "\\\"") + "\""
      }
    }
    */
}
