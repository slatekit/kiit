/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.common

import scala.reflect.runtime.universe.{Type,typeOf}
import slate.common.reflect.ReflectConsts._

object Converter {

  def convertToString(text:String) : String   = text

  def convertToBool  (text:String) : Boolean  = text.toBoolean

  def convertToShort (text:String) : Short    = text.toShort

  def convertToInt   (text:String) : Int      = text.toInt

  def convertToLong  (text:String) : Long     = text.toLong

  def convertToFloat (text:String) : Float    = text.toFloat

  def convertToDouble(text:String) : Double   = text.toDouble

  def convertToDate  (text:String) : DateTime = InputFuncs.convertDate(text)


  def converterFor(tpe:Type): (String) => Any = {
    val converter = tpe match {
      // Basic types
      case BoolType       => convertToBool   _
      case ShortType      => convertToShort  _
      case IntType        => convertToInt    _
      case LongType       => convertToLong   _
      case FloatType      => convertToFloat  _
      case DoubleType     => convertToDouble _
      case DateType       => convertToDate   _
      case StringType     => convertToString _
      case _              => convertToString _
    }
    converter
  }
}
