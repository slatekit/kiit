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

import slatekit.common.Reference
import slatekit.common.validations.ValidationConsts.Error_Contains
import slatekit.common.validations.ValidationConsts.Error_EndsWith
import slatekit.common.validations.ValidationConsts.Error_Has_CharsLCase
import slatekit.common.validations.ValidationConsts.Error_Has_CharsUCase
import slatekit.common.validations.ValidationConsts.Error_Has_Digits
import slatekit.common.validations.ValidationConsts.Error_Has_Symbols
import slatekit.common.validations.ValidationConsts.Error_Is_Alpha
import slatekit.common.validations.ValidationConsts.Error_Is_AlphaLowerCase
import slatekit.common.validations.ValidationConsts.Error_Is_AlphaNumeric
import slatekit.common.validations.ValidationConsts.Error_Is_AlphaUpperCase
import slatekit.common.validations.ValidationConsts.Error_Is_Between
import slatekit.common.validations.ValidationConsts.Error_Is_Email
import slatekit.common.validations.ValidationConsts.Error_Is_Empty
import slatekit.common.validations.ValidationConsts.Error_Is_Length
import slatekit.common.validations.ValidationConsts.Error_Is_MaxLength
import slatekit.common.validations.ValidationConsts.Error_Is_MaxValue
import slatekit.common.validations.ValidationConsts.Error_Is_MinLength
import slatekit.common.validations.ValidationConsts.Error_Is_MinValue
import slatekit.common.validations.ValidationConsts.Error_Is_NotEmpty
import slatekit.common.validations.ValidationConsts.Error_Is_Numeric
import slatekit.common.validations.ValidationConsts.Error_Is_OneOf
import slatekit.common.validations.ValidationConsts.Error_Is_PhoneUS
import slatekit.common.validations.ValidationConsts.Error_Is_SocialSecurity
import slatekit.common.validations.ValidationConsts.Error_Is_Url
import slatekit.common.validations.ValidationConsts.Error_Is_ZipCodeUS
import slatekit.common.validations.ValidationConsts.Error_Is_ZipCodeUSWithFour
import slatekit.common.validations.ValidationConsts.Error_StartsWith

object ValidationFuncsExt {

  // Empty / Non-Empty
  fun isEmpty          (text: String, ref: Reference? = null, error:String = Error_Is_Empty    )             : ValidationResult = buildResult( text.isNullOrEmpty(), ref, error)
  fun isNotEmpty       ( text: String, ref:Reference? = null, error:String = Error_Is_NotEmpty )             : ValidationResult = buildResult( !text.isNullOrEmpty(), ref, error)
  fun isOneOf          ( text: String, items:List<String>, ref:Reference?, error:String = Error_Is_OneOf )    : ValidationResult = buildResult( items.contains(text)          , ref, error )


  // Length functions
  fun isLength         ( text: String, len:Int, ref:Reference? = null, error:String = Error_Is_Length    )    : ValidationResult = buildResult( ValidationFuncs.isLength(text, len)   , ref, error )
  fun isMinLength      ( text: String, min:Int, ref:Reference? = null, error:String = Error_Is_MinLength )    : ValidationResult = buildResult( ValidationFuncs.isMinLength(text, min), ref, error )
  fun isMaxLength      ( text: String, max:Int, ref:Reference? = null, error:String = Error_Is_MaxLength )    : ValidationResult = buildResult( ValidationFuncs.isMaxLength(text, max), ref, error )


  // Numeric checks
  fun isMinValue       ( value:Int, min:Int, ref:Reference? = null, error:String = Error_Is_MinValue)         : ValidationResult = buildResult( ValidationFuncs.isMinValue(value, min), ref, error )
  fun isMaxValue       ( value:Int, max:Int, ref:Reference? = null, error:String = Error_Is_MaxValue)         : ValidationResult = buildResult( ValidationFuncs.isMaxValue(value, max), ref, error )
  fun isBetween        ( value:Int, min:Int, max:Int, ref:Reference?, error:String = Error_Is_Between)        : ValidationResult = buildResult( ValidationFuncs.isBetween(value, min, max)   , ref, error )


  // Char checks
  fun hasDigits        ( text:String, count:Int, ref:Reference? = null, error:String = Error_Has_Digits    )  : ValidationResult = buildResult( ValidationFuncs.hasDigits(text, count )    , ref, error )
  fun hasSymbols       ( text:String, count:Int, ref:Reference? = null, error:String = Error_Has_Symbols   )  : ValidationResult = buildResult( ValidationFuncs.hasSymbols(text, count )   , ref, error )
  fun hasCharsLCase    ( text:String, count:Int, ref:Reference? = null, error:String = Error_Has_CharsLCase)  : ValidationResult = buildResult( ValidationFuncs.hasCharsLCase(text, count ), ref, error )
  fun hasCharsUCase    ( text:String, count:Int, ref:Reference? = null, error:String = Error_Has_CharsUCase)  : ValidationResult = buildResult( ValidationFuncs.hasCharsUCase(text, count ), ref, error )


  // Content checks
  fun startsWith       ( text:String, expected:String, ref:Reference? = null, error:String = Error_StartsWith) : ValidationResult = buildResult( ValidationFuncs.startsWith(text, expected), ref, error )
  fun endsWith         ( text:String, expected:String, ref:Reference? = null, error:String = Error_EndsWith  ) : ValidationResult = buildResult( ValidationFuncs.endsWith  (text, expected), ref, error )
  fun contains         ( text:String, expected:String, ref:Reference? = null, error:String = Error_Contains  ) : ValidationResult = buildResult( ValidationFuncs.contains  (text, expected), ref, error )


  // Format checks
  fun isEmail            ( text: String, ref:Reference? = null, error:String = Error_Is_Email            ) : ValidationResult = buildResult( ValidationFuncs.isEmail            ( text ), ref, error )
  fun isAlpha            ( text: String, ref:Reference? = null, error:String = Error_Is_Alpha            ) : ValidationResult = buildResult( ValidationFuncs.isAlpha            ( text ), ref, error )
  fun isAlphaUpperCase   ( text: String, ref:Reference? = null, error:String = Error_Is_AlphaUpperCase   ) : ValidationResult = buildResult( ValidationFuncs.isAlphaUpperCase   ( text ), ref, error )
  fun isAlphaLowerCase   ( text: String, ref:Reference? = null, error:String = Error_Is_AlphaLowerCase   ) : ValidationResult = buildResult( ValidationFuncs.isAlphaLowerCase   ( text ), ref, error )
  fun isAlphaNumeric     ( text: String, ref:Reference? = null, error:String = Error_Is_AlphaNumeric     ) : ValidationResult = buildResult( ValidationFuncs.isAlphaNumeric     ( text ), ref, error )
  fun isNumeric          ( text: String, ref:Reference? = null, error:String = Error_Is_Numeric          ) : ValidationResult = buildResult( ValidationFuncs.isNumeric          ( text ), ref, error )
  fun isSocialSecurity   ( text: String, ref:Reference? = null, error:String = Error_Is_SocialSecurity   ) : ValidationResult = buildResult( ValidationFuncs.isSocialSecurity   ( text ), ref, error )
  fun isUrl              ( text: String, ref:Reference? = null, error:String = Error_Is_Url              ) : ValidationResult = buildResult( ValidationFuncs.isUrl              ( text ), ref, error )
  fun isZipCodeUS        ( text: String, ref:Reference? = null, error:String = Error_Is_ZipCodeUS        ) : ValidationResult = buildResult( ValidationFuncs.isZipCodeUS        ( text ), ref, error )
  fun isZipCodeUSWithFour( text: String, ref:Reference? = null, error:String = Error_Is_ZipCodeUSWithFour) : ValidationResult = buildResult( ValidationFuncs.isZipCodeUSWithFour( text ), ref, error )
  fun isPhoneUS          ( text: String, ref:Reference? = null, error:String = Error_Is_PhoneUS          ) : ValidationResult = buildResult( ValidationFuncs.isPhoneUS          ( text ), ref, error )


  fun buildResult(isValid:Boolean, ref:Reference?, error:String): ValidationResult =
    if(isValid){
      ValidationResult(true, error, ref, 1)
    }
    else {
      ValidationResult(false, error, ref, 0)
    }

}
