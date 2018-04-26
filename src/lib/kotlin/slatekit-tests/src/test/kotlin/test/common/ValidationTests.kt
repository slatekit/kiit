/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.common

import org.junit.Test
import slatekit.common.validations.ValidationFuncs.hasCharsLCase
import slatekit.common.validations.ValidationFuncs.hasCharsUCase
import slatekit.common.validations.ValidationFuncs.hasDigits
import slatekit.common.validations.ValidationFuncs.hasSymbols
import slatekit.common.validations.ValidationFuncs.isAlpha
import slatekit.common.validations.ValidationFuncs.isAlphaLowerCase
import slatekit.common.validations.ValidationFuncs.isAlphaNumeric
import slatekit.common.validations.ValidationFuncs.isAlphaUpperCase
import slatekit.common.validations.ValidationFuncs.isEmail
import slatekit.common.validations.ValidationFuncs.isEmpty
import slatekit.common.validations.ValidationFuncs.isLength
import slatekit.common.validations.ValidationFuncs.isMinValue
import slatekit.common.validations.ValidationFuncs.isMaxValue
import slatekit.common.validations.ValidationFuncs.isMinLength
import slatekit.common.validations.ValidationFuncs.isMaxLength
import slatekit.common.validations.ValidationFuncs.isNotEmpty
import slatekit.common.validations.ValidationFuncs.isNumeric
import slatekit.common.validations.ValidationFuncs.isPhoneUS
import slatekit.common.validations.ValidationFuncs.isUrl
import slatekit.common.validations.ValidationFuncs.isZipCodeUS

/**
 * Created by kishorereddy on 5/23/17.
 */

class ValidationTests {
    @Test fun is_Empty() {
        assert( isEmpty("") )
        assert( !isEmpty("123") )
    }


    @Test fun is_Non_Empty() {
        assert( isNotEmpty("123") )
        assert( !isNotEmpty("") )
    }


    @Test fun is_Length() {
        assert( isLength("123", 3) )
        assert( isLength("abc", 3) )
        assert( !isLength("", 3) )
        assert( !isLength("abc", 4) )
    }


    @Test fun is_Min_Length() {
        assert( !isMinLength("12" , 3) )
        assert( isMinLength("123" , 3) )
        assert( isMinLength("1234", 3) )
    }


    @Test fun is_Max_Length() {
        assert(  isMaxLength("12"  , 3) )
        assert(  isMaxLength("123" , 3) )
        assert( !isMaxLength("1234", 3) )
    }


    @Test fun is_Min_Value() {
        assert( !isMinValue( 99, 100) )
        assert(  isMinValue(100, 100) )
        assert(  isMinValue(101, 100) )
    }


    @Test fun is_Max_Value() {
        assert(  isMaxValue(99 , 100) )
        assert(  isMaxValue(100, 100) )
        assert( !isMaxValue(101, 100) )
    }


    @Test fun has_Digits() {
        assert( hasDigits("a1b2c3"  , 3) )
        assert( hasDigits("abc123"  , 3) )
        assert( hasDigits("1234abcd", 4) )
    }


    @Test fun has_Chars_Lower_case() {
        assert( hasCharsLCase("a1b2c3"  , 3) )
        assert( hasCharsLCase("abc123"  , 3) )
        assert( hasCharsLCase("1234abcd", 4) )
    }


    @Test fun has_Chars_Upper_case() {
        assert( hasCharsUCase("A1B2C3"  , 3) )
        assert( hasCharsUCase("ABC123"  , 3) )
        assert( hasCharsUCase("1234ABCD", 4) )
    }


    @Test fun has_Symbols() {
        assert( hasSymbols("A\$B%C^"  , 3) )
        assert( hasSymbols("ABC<>?"  , 3) )
        assert( hasSymbols("1234[]{}", 4) )
    }

    @Test fun regex_is_Email() {
        assert( !isEmail(""))
        assert( !isEmail("wonderwoman"))
        assert( !isEmail("@amazonian.com"))
        assert( !isEmail("wonderwoman_amazonian.com"))
        assert(  isEmail("wonderwoman@amazonian.com"))
    }


    @Test fun regex_is_Alpha() {
        assert( !isAlpha(""))
        assert( !isAlpha("[]{},."))
        assert( !isAlpha("123456"))
        assert(  isAlpha("abcdefg"))
        assert(  isAlpha("ABCDEFG"))
    }


    @Test fun regex_is_Alpha_Uppercase() {
        assert( !isAlphaUpperCase(""))
        assert( !isAlphaUpperCase("[]{},."))
        assert( !isAlphaUpperCase("123456"))
        assert( !isAlphaUpperCase("abcdefg"))
        assert(  isAlphaUpperCase("ABCDEFG"))
    }


    @Test fun regex_is_Alpha_Lowercase() {
        assert( !isAlphaLowerCase(""))
        assert( !isAlphaLowerCase("[]{},."))
        assert( !isAlphaLowerCase("123456"))
        assert(  isAlphaLowerCase("abcdefg"))
        assert( !isAlphaLowerCase("ABCDEFG"))
    }


    @Test fun regex_is_Alpha_Numeric() {
        assert( !isAlphaNumeric(""))
        assert( !isAlphaNumeric("[]{},."))
        assert(  isAlphaNumeric("123456"))
        assert(  isAlphaNumeric("abcdefg"))
        assert(  isAlphaNumeric("ABCDEFG"))
    }


    @Test fun regex_is_Numeric() {
        assert( !isNumeric(""))
        assert( !isNumeric("[]{},."))
        assert(  isNumeric("123456"))
        assert( !isNumeric("abcdefg"))
        assert( !isNumeric("ABCDEFG"))
    }


    @Test fun regex_is_Url() {
        assert( !isUrl(""))
        assert( !isUrl("[]{},."))
        assert( !isUrl("123456"))
        assert( !isUrl("abcdefg"))
        assert( !isUrl("ABCDEFG"))
        assert( !isUrl("http"))
        assert( !isUrl("http://"))
        assert( isUrl("http://slatekit.com"))
        assert( isUrl("http://www.slatekit.com"))
        assert( isUrl("http://www.slatekit.com/"))
        assert( isUrl("http://www.slatekit.com/about"))
        assert( isUrl("http://www.slatekit.com/about?"))
        assert( isUrl("http://www.slatekit.com/about.json"))
        assert( isUrl("http://www.slatekit.com/about?version=1.2"))
    }


    @Test fun regex_is_Phone_US() {
        assert( !isPhoneUS(""))
        assert( !isPhoneUS("[]{},."))
        assert( !isPhoneUS("123456"))
        assert( !isPhoneUS("abcdefg"))
        assert( !isPhoneUS("ABCDEFG"))
        assert(  isPhoneUS("123-456-7890"))
        assert(  isPhoneUS("1234567890"))
    }


    @Test fun regex_is_ZipCode_US() {
        assert( !isZipCodeUS(""))
        assert( !isZipCodeUS("[]{},."))
        assert( !isZipCodeUS("123456"))
        assert( !isZipCodeUS("abcdefg"))
        assert( !isZipCodeUS("ABCDEFG"))
        assert(  isZipCodeUS("12345"))
        assert(  isZipCodeUS("01234"))
    }
}