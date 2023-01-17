/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
 *  </kiit_header>
 */

package kiit.common.checks

import kiit.common.checks.CheckConsts.NUMS
import kiit.common.checks.CheckConsts.SYMS
import kiit.common.checks.CheckConsts.LETTERS_UCASE
import kiit.common.checks.CheckConsts.LETTERS_LCASE

object Check {

  // Empty / Non-Empty
  @JvmStatic fun isEmpty(text: String): Boolean = text.isNullOrEmpty()
  @JvmStatic fun isNotEmpty(text: String): Boolean = text.isNotEmpty()
  @JvmStatic fun isOneOf(text: String, items: List<String>): Boolean = items.contains(text)

  // Length @JvmStatic functions
  @JvmStatic fun isLength(text: String, len: Int): Boolean = !isEmpty(text) && text.length == len
  @JvmStatic fun isMinLength(text: String, min: Int): Boolean = !isEmpty(text) && text.length >= min
  @JvmStatic fun isMaxLength(text: String, max: Int): Boolean = !isEmpty(text) && text.length <= max

  // Numeric checks
  @JvmStatic fun isMinValue(value: Int, min: Int): Boolean = value >= min
  @JvmStatic fun isMaxValue(value: Int, max: Int): Boolean = value <= max
  @JvmStatic fun isBetween(value: Int, min: Int, max: Int): Boolean = isMinValue(value, min) && isMaxValue(value, max)

  // Char checks
  @JvmStatic fun hasDigits(text: String, count: Int): Boolean = contains(text, NUMS, count)
  @JvmStatic fun hasSymbols(text: String, count: Int): Boolean = contains(text, SYMS, count)
  @JvmStatic fun hasCharsLCase(text: String, count: Int): Boolean = contains(text, LETTERS_LCASE, count)
  @JvmStatic fun hasCharsUCase(text: String, count: Int): Boolean = contains(text, LETTERS_UCASE, count)

  // Content checks
  @JvmStatic fun startsWith(text: String, expected: String): Boolean = !text.isNullOrEmpty() && text.startsWith(expected)
  @JvmStatic fun endsWith(text: String, expected: String): Boolean = !text.isNullOrEmpty() && text.endsWith(expected)
  @JvmStatic fun contains(text: String, expected: String): Boolean = !text.isNullOrEmpty() && text.contains(expected)

  // Format checks
  @JvmStatic fun isEmail(text: String): Boolean = isMatch(Patterns.email, text)
  @JvmStatic fun isAlpha(text: String): Boolean = isMatch(Patterns.alpha, text)
  @JvmStatic fun isAlphaUpperCase(text: String): Boolean = isMatch(Patterns.alphaUpperCase, text)
  @JvmStatic fun isAlphaLowerCase(text: String): Boolean = isMatch(Patterns.alphaLowerCase, text)
  @JvmStatic fun isAlphaNumeric(text: String): Boolean = isMatch(Patterns.alphaNumeric, text)
  @JvmStatic fun isWholeNumber(text: String): Boolean = isMatch(Patterns.digits, text)
  @JvmStatic fun isNumeric(text: String): Boolean = isMatch(Patterns.numeric, text)
  @JvmStatic fun isSocialSecurity(text: String): Boolean = isMatch(Patterns.socialSecurity, text)
  @JvmStatic fun isUrl(text: String): Boolean = isMatch(Patterns.url, text)
  @JvmStatic fun isZipCodeUS(text: String): Boolean = isMatch(Patterns.zipCodeUS, text)
  @JvmStatic fun isZipCodeUSWithFour(text: String): Boolean = isMatch(Patterns.zipCodeUSWithFour, text)
  @JvmStatic fun isPhoneUS(text: String): Boolean = isMatch(Patterns.phoneUS, text)

  @JvmStatic fun isMatch(pattern: Pattern, text: String): Boolean =
    if (text.isNullOrEmpty()) {
      false
    } else {
      Regex(pattern.pattern).matches(text)
    }

  @JvmStatic fun contains(text: String, allowed: Map<Char, Boolean>, count: Int): Boolean {
    val total = text.fold(0, { i, c -> i + if (allowed.containsKey(c)) 1 else 0 })
    return total == count
  }
}