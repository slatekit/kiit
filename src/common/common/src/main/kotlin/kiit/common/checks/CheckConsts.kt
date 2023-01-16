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

import kiit.common.toCharMap

object CheckConsts {

    @JvmField val NUMS = "0123456789".toCharMap
    @JvmField val LETTERS_LCASE = "abcdefghijklmnopqrstuvwxyz".toCharMap
    @JvmField val LETTERS_UCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharMap
    @JvmField val LETTERS_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharMap
    @JvmField val ALPHA = "0123456789abcdefghijklmnopqrstuvwxyz".toCharMap
    @JvmField val SYMS = "!@#$%^&*()_+-=[]{}|;:,./<>?".toCharMap
    @JvmField val ALPHASYM = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()_+-=[]{}|;:,./<>?".toCharMap
    const val Error_Is_Empty = "Item must be empty"
    const val Error_Is_NotEmpty = "Item must not be empty"
    const val Error_Is_OneOf = "Item must be one of the valid values"
    const val Error_Is_Length = "Item must have a valid length"
    const val Error_Is_MinLength = "Item must have a valid min length"
    const val Error_Is_MaxLength = "Item must have a valid max length"
    const val Error_Is_MinValue = "Item must have a valid min value"
    const val Error_Is_MaxValue = "Item must have a valid max length"
    const val Error_Is_Between = "Item must be between the valid values"
    const val Error_Has_Digits = "Item must have digits"
    const val Error_Has_Symbols = "Item must have symbols"
    const val Error_Has_CharsLCase = "Item must have lower case letters"
    const val Error_Has_CharsUCase = "Item must have upper case letters"
    const val Error_StartsWith = "Item must have valid prefix"
    const val Error_EndsWith = "Item must have valid suffix"
    const val Error_Contains = "Item must have valid content"
    const val Error_Is_Email = "Item must be a valid email"
    const val Error_Is_Alpha = "Item must contain letters"
    const val Error_Is_AlphaUpperCase = "Item must contain upper case letters"
    const val Error_Is_AlphaLowerCase = "Item must contain lower case letters"
    const val Error_Is_AlphaNumeric = "Item must contain letters or numbers"
    const val Error_Is_Numeric = "Item must contain numbers"
    const val Error_Is_SocialSecurity = "Item must be a valid ssn"
    const val Error_Is_Url = "Item must be a valid url"
    const val Error_Is_ZipCodeUS = "Item must be a valid zip-code"
    const val Error_Is_ZipCodeUSWithFour = "Item must be a valid zip-code"
    const val Error_Is_PhoneUS = "Item must be a valid phone number"
}
