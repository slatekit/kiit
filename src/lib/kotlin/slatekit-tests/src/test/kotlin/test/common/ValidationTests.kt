/**
 <kiit_header>
url: www.slatekit.com
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package test.common

import org.junit.Assert
import org.junit.Test
import kiit.common.checks.Check.hasCharsLCase
import kiit.common.checks.Check.hasCharsUCase
import kiit.common.checks.Check.hasDigits
import kiit.common.checks.Check.hasSymbols
import kiit.common.checks.Check.isAlpha
import kiit.common.checks.Check.isAlphaLowerCase
import kiit.common.checks.Check.isAlphaNumeric
import kiit.common.checks.Check.isAlphaUpperCase
import kiit.common.checks.Check.isEmail
import kiit.common.checks.Check.isEmpty
import kiit.common.checks.Check.isLength
import kiit.common.checks.Check.isMinValue
import kiit.common.checks.Check.isMaxValue
import kiit.common.checks.Check.isMinLength
import kiit.common.checks.Check.isMaxLength
import kiit.common.checks.Check.isNotEmpty
import kiit.common.checks.Check.isNumeric
import kiit.common.checks.Check.isPhoneUS
import kiit.common.checks.Check.isUrl
import kiit.common.checks.Check.isZipCodeUS

/**
 * Created by kishorereddy on 5/23/17.
 */

class ValidationTests {
    @Test fun is_Empty() {
        Assert.assertTrue( isEmpty("") )
        Assert.assertTrue( !isEmpty("123") )
    }


    @Test fun is_Non_Empty() {
        Assert.assertTrue( isNotEmpty("123") )
        Assert.assertTrue( !isNotEmpty("") )
    }


    @Test fun is_Length() {
        Assert.assertTrue( isLength("123", 3) )
        Assert.assertTrue( isLength("abc", 3) )
        Assert.assertTrue( !isLength("", 3) )
        Assert.assertTrue( !isLength("abc", 4) )
    }


    @Test fun is_Min_Length() {
        Assert.assertTrue( !isMinLength("12" , 3) )
        Assert.assertTrue( isMinLength("123" , 3) )
        Assert.assertTrue( isMinLength("1234", 3) )
    }


    @Test fun is_Max_Length() {
        Assert.assertTrue(  isMaxLength("12"  , 3) )
        Assert.assertTrue(  isMaxLength("123" , 3) )
        Assert.assertTrue( !isMaxLength("1234", 3) )
    }


    @Test fun is_Min_Value() {
        Assert.assertTrue( !isMinValue( 99, 100) )
        Assert.assertTrue(  isMinValue(100, 100) )
        Assert.assertTrue(  isMinValue(101, 100) )
    }


    @Test fun is_Max_Value() {
        Assert.assertTrue(  isMaxValue(99 , 100) )
        Assert.assertTrue(  isMaxValue(100, 100) )
        Assert.assertTrue( !isMaxValue(101, 100) )
    }


    @Test fun has_Digits() {
        Assert.assertTrue( hasDigits("a1b2c3"  , 3) )
        Assert.assertTrue( hasDigits("abc123"  , 3) )
        Assert.assertTrue( hasDigits("1234abcd", 4) )
    }


    @Test fun has_Chars_Lower_case() {
        Assert.assertTrue( hasCharsLCase("a1b2c3"  , 3) )
        Assert.assertTrue( hasCharsLCase("abc123"  , 3) )
        Assert.assertTrue( hasCharsLCase("1234abcd", 4) )
    }


    @Test fun has_Chars_Upper_case() {
        Assert.assertTrue( hasCharsUCase("A1B2C3"  , 3) )
        Assert.assertTrue( hasCharsUCase("ABC123"  , 3) )
        Assert.assertTrue( hasCharsUCase("1234ABCD", 4) )
    }


    @Test fun has_Symbols() {
        Assert.assertTrue( hasSymbols("A\$B%C^"  , 3) )
        Assert.assertTrue( hasSymbols("ABC<>?"  , 3) )
        Assert.assertTrue( hasSymbols("1234[]{}", 4) )
    }

    @Test fun regex_is_Email() {
        Assert.assertTrue( !isEmail(""))
        Assert.assertTrue( !isEmail("wonderwoman"))
        Assert.assertTrue( !isEmail("@amazonian.com"))
        Assert.assertTrue( !isEmail("wonderwoman_amazonian.com"))
        Assert.assertTrue(  isEmail("wonderwoman@amazonian.com"))
        Assert.assertTrue(  isEmail("wonder.woman@amazon-ian.com"))
        Assert.assertTrue(  isEmail("wonder-woman@amazon_ian.com"))
        Assert.assertTrue(  isEmail("wonder_woman@amazonian.com"))
    }


    @Test fun regex_is_Alpha() {
        Assert.assertTrue( !isAlpha(""))
        Assert.assertTrue( !isAlpha("[]{},."))
        Assert.assertTrue( !isAlpha("123456"))
        Assert.assertTrue(  isAlpha("abcdefg"))
        Assert.assertTrue(  isAlpha("ABCDEFG"))
    }


    @Test fun regex_is_Alpha_Uppercase() {
        Assert.assertTrue( !isAlphaUpperCase(""))
        Assert.assertTrue( !isAlphaUpperCase("[]{},."))
        Assert.assertTrue( !isAlphaUpperCase("123456"))
        Assert.assertTrue( !isAlphaUpperCase("abcdefg"))
        Assert.assertTrue(  isAlphaUpperCase("ABCDEFG"))
    }


    @Test fun regex_is_Alpha_Lowercase() {
        Assert.assertTrue( !isAlphaLowerCase(""))
        Assert.assertTrue( !isAlphaLowerCase("[]{},."))
        Assert.assertTrue( !isAlphaLowerCase("123456"))
        Assert.assertTrue(  isAlphaLowerCase("abcdefg"))
        Assert.assertTrue( !isAlphaLowerCase("ABCDEFG"))
    }


    @Test fun regex_is_Alpha_Numeric() {
        Assert.assertTrue( !isAlphaNumeric(""))
        Assert.assertTrue( !isAlphaNumeric("[]{},."))
        Assert.assertTrue(  isAlphaNumeric("123456"))
        Assert.assertTrue(  isAlphaNumeric("abcdefg"))
        Assert.assertTrue(  isAlphaNumeric("ABCDEFG"))
    }


    @Test fun regex_is_Numeric() {
        Assert.assertTrue( !isNumeric(""))
        Assert.assertTrue( !isNumeric("[]{},."))
        Assert.assertTrue(  isNumeric("123456"))
        Assert.assertTrue( !isNumeric("abcdefg"))
        Assert.assertTrue( !isNumeric("ABCDEFG"))
    }


    @Test fun regex_is_Url() {
        Assert.assertTrue( !isUrl(""))
        Assert.assertTrue( !isUrl("[]{},."))
        Assert.assertTrue( !isUrl("123456"))
        Assert.assertTrue( !isUrl("abcdefg"))
        Assert.assertTrue( !isUrl("ABCDEFG"))
        Assert.assertTrue( !isUrl("http"))
        Assert.assertTrue( !isUrl("http://"))
        Assert.assertTrue( isUrl("http://slatekit.com"))
        Assert.assertTrue( isUrl("http://www.slatekit.com"))
        Assert.assertTrue( isUrl("http://www.slatekit.com/"))
        Assert.assertTrue( isUrl("http://www.slatekit.com/about"))
        Assert.assertTrue( isUrl("http://www.slatekit.com/about?"))
        Assert.assertTrue( isUrl("http://www.slatekit.com/about.json"))
        Assert.assertTrue( isUrl("http://www.slatekit.com/about?version=1.2"))
    }


    @Test fun regex_is_Phone_US() {
        Assert.assertTrue( !isPhoneUS(""))
        Assert.assertTrue( !isPhoneUS("[]{},."))
        Assert.assertTrue( !isPhoneUS("123456"))
        Assert.assertTrue( !isPhoneUS("abcdefg"))
        Assert.assertTrue( !isPhoneUS("ABCDEFG"))
        Assert.assertTrue(  isPhoneUS("123-456-7890"))
        Assert.assertTrue(  isPhoneUS("1234567890"))
    }


    @Test fun regex_is_ZipCode_US() {
        Assert.assertTrue( !isZipCodeUS(""))
        Assert.assertTrue( !isZipCodeUS("[]{},."))
        Assert.assertTrue( !isZipCodeUS("123456"))
        Assert.assertTrue( !isZipCodeUS("abcdefg"))
        Assert.assertTrue( !isZipCodeUS("ABCDEFG"))
        Assert.assertTrue(  isZipCodeUS("12345"))
        Assert.assertTrue(  isZipCodeUS("01234"))
    }
}