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

import slatekit.common.toCharMap

object ValidationConsts {

    @JvmField val NUMS = "0123456789".toCharMap
    @JvmField val LETTERS_LCASE = "abcdefghijklmnopqrstuvwxyz".toCharMap
    @JvmField val LETTERS_UCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharMap
    @JvmField val LETTERS_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharMap
    @JvmField val ALPHA = "0123456789abcdefghijklmnopqrstuvwxyz".toCharMap
    @JvmField val SYMS = "!@#$%^&*()_+-=[]{}|;:,./<>?".toCharMap
    @JvmField val ALPHASYM = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()_+-=[]{}|;:,./<>?".toCharMap
    @JvmField val Error_Is_Empty = "Item must be empty"
    @JvmField val Error_Is_NotEmpty = "Item must not be empty"
    @JvmField val Error_Is_OneOf = "Item must be one of the valid values"
    @JvmField val Error_Is_Length = "Item must have a valid length"
    @JvmField val Error_Is_MinLength = "Item must have a valid min length"
    @JvmField val Error_Is_MaxLength = "Item must have a valid max length"
    @JvmField val Error_Is_MinValue = "Item must have a valid min value"
    @JvmField val Error_Is_MaxValue = "Item must have a valid max length"
    @JvmField val Error_Is_Between = "Item must be between the valid values"
    @JvmField val Error_Has_Digits = "Item must have digits"
    @JvmField val Error_Has_Symbols = "Item must have symbols"
    @JvmField val Error_Has_CharsLCase = "Item must have lower case letters"
    @JvmField val Error_Has_CharsUCase = "Item must have upper case letters"
    @JvmField val Error_StartsWith = "Item must have valid prefix"
    @JvmField val Error_EndsWith = "Item must have valid suffix"
    @JvmField val Error_Contains = "Item must have valid content"
    @JvmField val Error_Is_Email = "Item must be a valid email"
    @JvmField val Error_Is_Alpha = "Item must contain letters"
    @JvmField val Error_Is_AlphaUpperCase = "Item must contain upper case letters"
    @JvmField val Error_Is_AlphaLowerCase = "Item must contain lower case letters"
    @JvmField val Error_Is_AlphaNumeric = "Item must contain letters or numbers"
    @JvmField val Error_Is_Numeric = "Item must contain numbers"
    @JvmField val Error_Is_SocialSecurity = "Item must be a valid ssn"
    @JvmField val Error_Is_Url = "Item must be a valid url"
    @JvmField val Error_Is_ZipCodeUS = "Item must be a valid zip-code"
    @JvmField val Error_Is_ZipCodeUSWithFour = "Item must be a valid zip-code"
    @JvmField val Error_Is_PhoneUS = "Item must be a valid phone number"
}
