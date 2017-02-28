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

import slate.common._
import ValidationConsts._

object ValidationFuncsExt {

  // Empty / Non-Empty
  def isEmpty          ( text: String, ref:Option[Reference] = None, error:String = Error_Is_Empty    )             : ValidationResult = buildResult( Strings.isNullOrEmpty( text  ), ref, error)
  def isNotEmpty       ( text: String, ref:Option[Reference] = None, error:String = Error_Is_NotEmpty )             : ValidationResult = buildResult( !Strings.isNullOrEmpty( text ), ref, error)
  def isOneOf          ( text: String, items:Seq[String], ref:Option[Reference], error:String = Error_Is_OneOf )    : ValidationResult = buildResult( items.contains(text)          , ref, error )


  // Length functions
  def isLength         ( text: String, len:Int, ref:Option[Reference] = None, error:String = Error_Is_Length    )    : ValidationResult = buildResult( ValidationFuncs.isLength(text, len)   , ref, error )
  def isMinLength      ( text: String, min:Int, ref:Option[Reference] = None, error:String = Error_Is_MinLength )    : ValidationResult = buildResult( ValidationFuncs.isMinLength(text, min), ref, error )
  def isMaxLength      ( text: String, max:Int, ref:Option[Reference] = None, error:String = Error_Is_MaxLength )    : ValidationResult = buildResult( ValidationFuncs.isMaxLength(text, max), ref, error )


  // Numeric checks
  def isMinValue       ( value:Int, min:Int, ref:Option[Reference] = None, error:String = Error_Is_MinValue)         : ValidationResult = buildResult( ValidationFuncs.isMinValue(value, min), ref, error )
  def isMaxValue       ( value:Int, max:Int, ref:Option[Reference] = None, error:String = Error_Is_MaxValue)         : ValidationResult = buildResult( ValidationFuncs.isMaxValue(value, max), ref, error )
  def isBetween        ( value:Int, min:Int, max:Int, ref:Option[Reference], error:String = Error_Is_Between)        : ValidationResult = buildResult( ValidationFuncs.isBetween(value, min, max)   , ref, error )


  // Char checks
  def hasDigits        ( text:String, count:Int, ref:Option[Reference] = None, error:String = Error_Has_Digits    )  : ValidationResult = buildResult( ValidationFuncs.hasDigits(text, count )    , ref, error )
  def hasSymbols       ( text:String, count:Int, ref:Option[Reference] = None, error:String = Error_Has_Symbols   )  : ValidationResult = buildResult( ValidationFuncs.hasSymbols(text, count )   , ref, error )
  def hasCharsLCase    ( text:String, count:Int, ref:Option[Reference] = None, error:String = Error_Has_CharsLCase)  : ValidationResult = buildResult( ValidationFuncs.hasCharsLCase(text, count ), ref, error )
  def hasCharsUCase    ( text:String, count:Int, ref:Option[Reference] = None, error:String = Error_Has_CharsUCase)  : ValidationResult = buildResult( ValidationFuncs.hasCharsUCase(text, count ), ref, error )


  // Content checks
  def startsWith       ( text:String, expected:String, ref:Option[Reference] = None, error:String = Error_StartsWith) : ValidationResult = buildResult( ValidationFuncs.startsWith(text, expected), ref, error )
  def endsWith         ( text:String, expected:String, ref:Option[Reference] = None, error:String = Error_EndsWith  ) : ValidationResult = buildResult( ValidationFuncs.endsWith  (text, expected), ref, error )
  def contains         ( text:String, expected:String, ref:Option[Reference] = None, error:String = Error_Contains  ) : ValidationResult = buildResult( ValidationFuncs.contains  (text, expected), ref, error )


  // Format checks
  def isEmail            ( text: String, ref:Option[Reference] = None, error:String = Error_Is_Email            ) : ValidationResult = buildResult( ValidationFuncs.isEmail            ( text ), ref, error )
  def isAlpha            ( text: String, ref:Option[Reference] = None, error:String = Error_Is_Alpha            ) : ValidationResult = buildResult( ValidationFuncs.isAlpha            ( text ), ref, error )
  def isAlphaUpperCase   ( text: String, ref:Option[Reference] = None, error:String = Error_Is_AlphaUpperCase   ) : ValidationResult = buildResult( ValidationFuncs.isAlphaUpperCase   ( text ), ref, error )
  def isAlphaLowerCase   ( text: String, ref:Option[Reference] = None, error:String = Error_Is_AlphaLowerCase   ) : ValidationResult = buildResult( ValidationFuncs.isAlphaLowerCase   ( text ), ref, error )
  def isAlphaNumeric     ( text: String, ref:Option[Reference] = None, error:String = Error_Is_AlphaNumeric     ) : ValidationResult = buildResult( ValidationFuncs.isAlphaNumeric     ( text ), ref, error )
  def isNumeric          ( text: String, ref:Option[Reference] = None, error:String = Error_Is_Numeric          ) : ValidationResult = buildResult( ValidationFuncs.isNumeric          ( text ), ref, error )
  def isSocialSecurity   ( text: String, ref:Option[Reference] = None, error:String = Error_Is_SocialSecurity   ) : ValidationResult = buildResult( ValidationFuncs.isSocialSecurity   ( text ), ref, error )
  def isUrl              ( text: String, ref:Option[Reference] = None, error:String = Error_Is_Url              ) : ValidationResult = buildResult( ValidationFuncs.isUrl              ( text ), ref, error )
  def isZipCodeUS        ( text: String, ref:Option[Reference] = None, error:String = Error_Is_ZipCodeUS        ) : ValidationResult = buildResult( ValidationFuncs.isZipCodeUS        ( text ), ref, error )
  def isZipCodeUSWithFour( text: String, ref:Option[Reference] = None, error:String = Error_Is_ZipCodeUSWithFour) : ValidationResult = buildResult( ValidationFuncs.isZipCodeUSWithFour( text ), ref, error )
  def isPhoneUS          ( text: String, ref:Option[Reference] = None, error:String = Error_Is_PhoneUS          ) : ValidationResult = buildResult( ValidationFuncs.isPhoneUS          ( text ), ref, error )


  def buildResult(isValid:Boolean, ref:Option[Reference], error:String): ValidationResult = {
    if(isValid){
      ValidationResult(true, Option(error), ref, 1)
    }
    else {
      ValidationResult(false, Option(error), ref, 0)
    }
  }
}
