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

    val NUMS          = "0123456789".toCharMap
    val LETTERS_LCASE = "abcdefghijklmnopqrstuvwxyz".toCharMap
    val LETTERS_UCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharMap
    val LETTERS_ALL   = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharMap
    val ALPHA         = "0123456789abcdefghijklmnopqrstuvwxyz".toCharMap
    val SYMS          = "!@#$%^&*()_+-=[]{}|;:,./<>?".toCharMap
    val ALPHASYM      = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()_+-=[]{}|;:,./<>?".toCharMap
    val Error_Is_Empty              = "Item must be empty"
    val Error_Is_NotEmpty           = "Item must not be empty"
    val Error_Is_OneOf              = "Item must be one of the valid values"
    val Error_Is_Length             = "Item must have a valid length"
    val Error_Is_MinLength          = "Item must have a valid min length"
    val Error_Is_MaxLength          = "Item must have a valid max length"
    val Error_Is_MinValue           = "Item must have a valid min value"
    val Error_Is_MaxValue           = "Item must have a valid max length"
    val Error_Is_Between            = "Item must be between the valid values"
    val Error_Has_Digits            = "Item must have digits"
    val Error_Has_Symbols           = "Item must have symbols"
    val Error_Has_CharsLCase        = "Item must have lower case letters"
    val Error_Has_CharsUCase        = "Item must have upper case letters"
    val Error_StartsWith            = "Item must have valid prefix"
    val Error_EndsWith              = "Item must have valid suffix"
    val Error_Contains              = "Item must have valid content"
    val Error_Is_Email              = "Item must be a valid email"
    val Error_Is_Alpha              = "Item must contain letters"
    val Error_Is_AlphaUpperCase     = "Item must contain upper case letters"
    val Error_Is_AlphaLowerCase     = "Item must contain lower case letters"
    val Error_Is_AlphaNumeric       = "Item must contain letters or numbers"
    val Error_Is_Numeric            = "Item must contain numbers"
    val Error_Is_SocialSecurity     = "Item must be a valid ssn"
    val Error_Is_Url                = "Item must be a valid url"
    val Error_Is_ZipCodeUS          = "Item must be a valid zip-code"
    val Error_Is_ZipCodeUSWithFour  = "Item must be a valid zip-code"
    val Error_Is_PhoneUS            = "Item must be a valid phone number"
}
