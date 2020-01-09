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
import slatekit.results.*

object Validations {

  // Empty / Non-Empty
  @JvmStatic fun isEmpty(text: String, error: String = Error_Is_Empty): Outcome<String> = buildResult(text, text.isNullOrEmpty(), error)
  @JvmStatic fun isNotEmpty(text: String, error: String = Error_Is_NotEmpty): Outcome<String> = buildResult(text, !text.isNullOrEmpty(), error)
  @JvmStatic fun isOneOf(text: String, items: List<String>, error: String = Error_Is_OneOf): Outcome<String> = buildResult(text, items.contains(text), error)

  // Length functions
  @JvmStatic fun isLength(text: String, len: Int, error: String = Error_Is_Length): Outcome<String> = buildResult(text, ValidationFuncs.isLength(text, len), error)
  @JvmStatic fun isMinLength(text: String, min: Int, error: String = Error_Is_MinLength): Outcome<String> = buildResult(text, ValidationFuncs.isMinLength(text, min), error)
  @JvmStatic fun isMaxLength(text: String, max: Int, error: String = Error_Is_MaxLength): Outcome<String> = buildResult(text, ValidationFuncs.isMaxLength(text, max), error)

  // Numeric checks
  @JvmStatic fun isMinValue(value: Int, min: Int, error: String = Error_Is_MinValue): Outcome<Int> = buildResult(value, ValidationFuncs.isMinValue(value, min), error)
  @JvmStatic fun isMaxValue(value: Int, max: Int, error: String = Error_Is_MaxValue): Outcome<Int> = buildResult(value, ValidationFuncs.isMaxValue(value, max), error)
  @JvmStatic fun isBetween(value: Int, min: Int, max: Int, error: String = Error_Is_Between): Outcome<Int> = buildResult(value, ValidationFuncs.isBetween(value, min, max), error)

  // Char checks
  @JvmStatic fun hasDigits(text: String, count: Int, error: String = Error_Has_Digits): Outcome<String> = buildResult(text, ValidationFuncs.hasDigits(text, count), error)
  @JvmStatic fun hasSymbols(text: String, count: Int, error: String = Error_Has_Symbols): Outcome<String> = buildResult(text, ValidationFuncs.hasSymbols(text, count), error)
  @JvmStatic fun hasCharsLCase(text: String, count: Int, error: String = Error_Has_CharsLCase): Outcome<String> = buildResult(text, ValidationFuncs.hasCharsLCase(text, count), error)
  @JvmStatic fun hasCharsUCase(text: String, count: Int, error: String = Error_Has_CharsUCase): Outcome<String> = buildResult(text, ValidationFuncs.hasCharsUCase(text, count), error)

  // Content checks
  @JvmStatic fun startsWith(text: String, expected: String, error: String = Error_StartsWith): Outcome<String> = buildResult(text, ValidationFuncs.startsWith(text, expected), error)
  @JvmStatic fun endsWith(text: String, expected: String, error: String = Error_EndsWith): Outcome<String> = buildResult(text, ValidationFuncs.endsWith(text, expected), error)
  @JvmStatic fun contains(text: String, expected: String, error: String = Error_Contains): Outcome<String> = buildResult(text, ValidationFuncs.contains(text, expected), error)

  // Format checks
  @JvmStatic fun isEmail(text: String, error: String = Error_Is_Email): Outcome<String> = buildResult(text, ValidationFuncs.isEmail(text), error)
  @JvmStatic fun isAlpha(text: String, error: String = Error_Is_Alpha): Outcome<String> = buildResult(text, ValidationFuncs.isAlpha(text), error)
  @JvmStatic fun isAlphaUpperCase(text: String, error: String = Error_Is_AlphaUpperCase): Outcome<String> = buildResult(text, ValidationFuncs.isAlphaUpperCase(text), error)
  @JvmStatic fun isAlphaLowerCase(text: String, error: String = Error_Is_AlphaLowerCase): Outcome<String> = buildResult(text, ValidationFuncs.isAlphaLowerCase(text), error)
  @JvmStatic fun isAlphaNumeric(text: String, error: String = Error_Is_AlphaNumeric): Outcome<String> = buildResult(text, ValidationFuncs.isAlphaNumeric(text), error)
  @JvmStatic fun isNumeric(text: String, error: String = Error_Is_Numeric): Outcome<String> = buildResult(text, ValidationFuncs.isNumeric(text), error)
  @JvmStatic fun isSocialSecurity(text: String, error: String = Error_Is_SocialSecurity): Outcome<String> = buildResult(text, ValidationFuncs.isSocialSecurity(text), error)
  @JvmStatic fun isUrl(text: String, error: String = Error_Is_Url): Outcome<String> = buildResult(text, ValidationFuncs.isUrl(text), error)
  @JvmStatic fun isZipCodeUS(text: String, error: String = Error_Is_ZipCodeUS): Outcome<String> = buildResult(text, ValidationFuncs.isZipCodeUS(text), error)
  @JvmStatic fun isZipCodeUSWithFour(text: String, error: String = Error_Is_ZipCodeUSWithFour): Outcome<String> = buildResult(text, ValidationFuncs.isZipCodeUSWithFour(text), error)
  @JvmStatic fun isPhoneUS(text: String, error: String = Error_Is_PhoneUS): Outcome<String> = buildResult(text, ValidationFuncs.isPhoneUS(text), error)

  @JvmStatic
  fun <T, I, E> collect(model:T, f:Validations.() -> List<Result<I, E>>): Validated<T> {
    val results = f()
    val firstFailure = results.firstOrNull { !it.success }
    val result:Validated<T> = when(firstFailure) {
      null -> Success(model)
      else -> {
        val failed = results.filter {!it.success }
        val errors = failed.map { (it as Failure<E> ).error }
        val list = Err.ErrorList(errors.map { Err.build(it) }, "Failed")
        Failure(list)
      }
    }
    return result
  }

  @JvmStatic fun <T> buildResult(input:T, isValid: Boolean, error: String): Outcome<T> =
    if (isValid) {
      Success(input)
    } else {
      Failure(Err.of(error))
    }
}
