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

package slatekit.common.validations

import slatekit.common.Pattern
import slatekit.common.Patterns
import slatekit.common.validations.ValidationConsts.NUMS
import slatekit.common.validations.ValidationConsts.SYMS
import slatekit.common.validations.ValidationConsts.LETTERS_UCASE
import slatekit.common.validations.ValidationConsts.LETTERS_LCASE

object ValidationFuncs {

  // Empty / Non-Empty
  fun isEmpty             ( text: String ) : Boolean = text.isNullOrEmpty()
  fun isNotEmpty          ( text: String ) : Boolean = text.isNotEmpty()
  fun isOneOf             ( text: String, items:List<String>) : Boolean = items.contains(text)


  // Length functions
  fun isLength            ( text: String, len:Int ) : Boolean = !isEmpty(text) && text.length == len
  fun isMinLength         ( text: String, min:Int ) : Boolean = !isEmpty(text) && text.length >= min
  fun isMaxLength         ( text: String, max:Int ) : Boolean = !isEmpty(text) && text.length <= max


  // Numeric checks
  fun isMinValue          ( value:Int, min:Int ) : Boolean = value >= min
  fun isMaxValue          ( value:Int, max:Int ) : Boolean = value <= max
  fun isBetween           ( value:Int, min:Int, max:Int): Boolean = isMinValue(value, min) && isMaxValue(value,max)

  // Char checks
  fun hasDigits           ( text:String, count:Int ) : Boolean = contains(text, NUMS, count)
  fun hasSymbols          ( text:String, count:Int ) : Boolean = contains(text, SYMS, count)
  fun hasCharsLCase       ( text:String, count:Int ) : Boolean = contains(text, LETTERS_LCASE, count)
  fun hasCharsUCase       ( text:String, count:Int ) : Boolean = contains(text, LETTERS_UCASE, count)


  // Content checks
  fun startsWith         ( text:String, expected:String) : Boolean = !text.isNullOrEmpty() && text.startsWith(expected)
  fun endsWith           ( text:String, expected:String) : Boolean = !text.isNullOrEmpty() && text.endsWith(expected)
  fun contains           ( text:String, expected:String) : Boolean = !text.isNullOrEmpty() && text.contains(expected)


  // Format checks
  fun isEmail             ( text: String ) : Boolean = isMatch( Patterns.email            , text )
  fun isAlpha             ( text: String ) : Boolean = isMatch( Patterns.alpha            , text )
  fun isAlphaUpperCase    ( text: String ) : Boolean = isMatch( Patterns.alphaUpperCase   , text )
  fun isAlphaLowerCase    ( text: String ) : Boolean = isMatch( Patterns.alphaLowerCase   , text )
  fun isAlphaNumeric      ( text: String ) : Boolean = isMatch( Patterns.alphaNumeric     , text )
  fun isNumeric           ( text: String ) : Boolean = isMatch( Patterns.numeric          , text )
  fun isSocialSecurity    ( text: String ) : Boolean = isMatch( Patterns.socialSecurity   , text )
  fun isUrl               ( text: String ) : Boolean = isMatch( Patterns.url              , text )
  fun isZipCodeUS         ( text: String ) : Boolean = isMatch( Patterns.zipCodeUS        , text )
  fun isZipCodeUSWithFour ( text: String ) : Boolean = isMatch( Patterns.zipCodeUSWithFour, text )
  fun isPhoneUS           ( text: String ) : Boolean = isMatch( Patterns.phoneUS          , text )


  fun isMatch(pattern:Pattern, text:String): Boolean =
    if(text.isNullOrEmpty()) {
      false
    }
    else {
      Regex(pattern.pattern).matches(text)
    }



  fun contains(text:String, allowed:Map<Char,Boolean>, count:Int):Boolean {
    val total = text.fold( 0, { i, c -> i + if ( allowed.containsKey(c) ) 1 else 0 })
    return total == count
  }

}