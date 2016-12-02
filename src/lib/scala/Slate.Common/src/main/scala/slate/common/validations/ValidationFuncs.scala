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

package slate.common.validations

import slate.common.RegexPatterns._
import slate.common.{Reference, Strings}
import ValidationConsts._

import scala.util.matching.Regex

object ValidationFuncs {

  // Empty / Non-Empty
  def isEmpty             ( text: String ) : Boolean = Strings.isNullOrEmpty( text  )
  def isNotEmpty          ( text: String ) : Boolean = !Strings.isNullOrEmpty( text )
  def isOneOf             ( text: String, items:Seq[String]) : Boolean = items.contains(text)


  // Length functions
  def isLength            ( text: String, len:Int ) : Boolean = !isEmpty(text) && text.length == len
  def isMinLength         ( text: String, min:Int ) : Boolean = !isEmpty(text) && text.length >= min
  def isMaxLength         ( text: String, max:Int ) : Boolean = !isEmpty(text) && text.length <= max


  // Numeric checks
  def isMinValue          ( value:Int, min:Int ) : Boolean = value >= min
  def isMaxValue          ( value:Int, max:Int ) : Boolean = value <= max
  def isBetween           ( value:Int, min:Int, max:Int): Boolean = isMinValue(value, min) && isMaxValue(value,max)

  // Char checks
  def hasDigits           ( text:String, count:Int ) : Boolean = contains(text, NUMS, count)
  def hasSymbols          ( text:String, count:Int ) : Boolean = contains(text, SYMS, count)
  def hasCharsLCase       ( text:String, count:Int ) : Boolean = contains(text, LETTERS_LCASE, count)
  def hasCharsUCase       ( text:String, count:Int ) : Boolean = contains(text, LETTERS_UCASE, count)


  // Content checks
  def startsWith         ( text:String, expected:String) : Boolean = !Strings.isNullOrEmpty(text) && text.startsWith(expected)
  def endsWith           ( text:String, expected:String) : Boolean = !Strings.isNullOrEmpty(text) && text.endsWith(expected)
  def contains           ( text:String, expected:String) : Boolean = !Strings.isNullOrEmpty(text) && text.contains(expected)


  // Format checks
  def isEmail             ( text: String ) : Boolean = isMatch( email            , text )
  def isAlpha             ( text: String ) : Boolean = isMatch( alpha            , text )
  def isAlphaUpperCase    ( text: String ) : Boolean = isMatch( alphaUpperCase   , text )
  def isAlphaLowerCase    ( text: String ) : Boolean = isMatch( alphaLowerCase   , text )
  def isAlphaNumeric      ( text: String ) : Boolean = isMatch( alphaNumeric     , text )
  def isNumeric           ( text: String ) : Boolean = isMatch( numeric          , text )
  def isSocialSecurity    ( text: String ) : Boolean = isMatch( socialSecurity   , text )
  def isUrl               ( text: String ) : Boolean = isMatch( url              , text )
  def isZipCodeUS         ( text: String ) : Boolean = isMatch( zipCodeUS        , text )
  def isZipCodeUSWithFour ( text: String ) : Boolean = isMatch( zipCodeUSWithFour, text )
  def isPhoneUS           ( text: String ) : Boolean = isMatch( phoneUS          , text )


  def isMatch(pattern:String, text:String): Boolean = {
    if(Strings.isNullOrEmpty(text)) return false
    val result = new Regex(pattern).findFirstIn(text)
    if(result.isDefined) true else false
  }


  def contains(text:String, allowed:Map[Char,Boolean], count:Int):Boolean =
  {
    var matched = 0
    for(ch <- text ){
      if(allowed.contains(ch))
      {
        matched += 1
      }
    }
    matched == count
  }

}