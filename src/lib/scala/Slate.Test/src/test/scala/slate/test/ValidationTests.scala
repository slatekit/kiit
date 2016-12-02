/**
*<slate_header>
  *author: Kishore Reddy
  *url: https://github.com/kishorereddy/scala-slate
  *copyright: 2015 Kishore Reddy
  *license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  *desc: a scala micro-framework
  *usage: Please refer to license on github for more info.
*</slate_header>
  */

package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec}
import slate.common.{RefField, Result}
import slate.common.validations._
import slate.common.validations.ValidationConsts._
import slate.common.validations.Validations._
import slate.common.validations.ValidationFuncs._
import slate.test.common.User


class ValidationTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }



  describe("Basic Checks") {

    it("Is Empty") {
      assert( isEmpty(null) )
      assert( isEmpty("") )
      assert( !isEmpty("123") )
    }


    it("Is Non Empty") {
      assert( isNotEmpty("123") )
      assert( !isNotEmpty(null) )
      assert( !isNotEmpty("") )
    }


    it("Is Length") {
      assert( isLength("123", 3) )
      assert( isLength("abc", 3) )
      assert( !isLength(null, 3) )
      assert( !isLength("", 3) )
      assert( !isLength("abc", 4) )
    }


    it("Is Min Length") {
      assert( !isMinLength("12" , 3) )
      assert( isMinLength("123" , 3) )
      assert( isMinLength("1234", 3) )
    }


    it("Is Max Length") {
      assert(  isMaxLength("12"  , 3) )
      assert(  isMaxLength("123" , 3) )
      assert( !isMaxLength("1234", 3) )
    }


    it("Is Min Value") {
      assert( !isMinValue( 99, 100) )
      assert(  isMinValue(100, 100) )
      assert(  isMinValue(101, 100) )
    }


    it("Is Max Value") {
      assert(  isMaxValue(99 , 100) )
      assert(  isMaxValue(100, 100) )
      assert( !isMaxValue(101, 100) )
    }


    it("Has Digits") {
      assert( hasDigits("a1b2c3"  , 3) )
      assert( hasDigits("abc123"  , 3) )
      assert( hasDigits("1234abcd", 4) )
    }


    it("Has Chars Lower case") {
      assert( hasCharsLCase("a1b2c3"  , 3) )
      assert( hasCharsLCase("abc123"  , 3) )
      assert( hasCharsLCase("1234abcd", 4) )
    }


    it("Has Chars Upper case") {
      assert( hasCharsUCase("A1B2C3"  , 3) )
      assert( hasCharsUCase("ABC123"  , 3) )
      assert( hasCharsUCase("1234ABCD", 4) )
    }


    it("Has Symbols") {
      assert( hasSymbols("A$B%C^"  , 3) )
      assert( hasSymbols("ABC<>?"  , 3) )
      assert( hasSymbols("1234[]{}", 4) )
    }
  }

  describe("Basic Regex") {

    it("Is Email") {
      assert( !isEmail(null))
      assert( !isEmail(""))
      assert( !isEmail("wonderwoman"))
      assert( !isEmail("@amazonian.com"))
      assert( !isEmail("wonderwoman_amazonian.com"))
      assert(  isEmail("wonderwoman@amazonian.com"))
    }


    it("Is Alpha") {
      assert( !isAlpha(null))
      assert( !isAlpha(""))
      assert( !isAlpha("[]{},."))
      assert( !isAlpha("123456"))
      assert(  isAlpha("abcdefg"))
      assert(  isAlpha("ABCDEFG"))
    }


    it("Is Alpha Uppercase") {
      assert( !isAlphaUpperCase(null))
      assert( !isAlphaUpperCase(""))
      assert( !isAlphaUpperCase("[]{},."))
      assert( !isAlphaUpperCase("123456"))
      assert( !isAlphaUpperCase("abcdefg"))
      assert(  isAlphaUpperCase("ABCDEFG"))
    }


    it("Is Alpha Lowercase") {
      assert( !isAlphaLowerCase(null))
      assert( !isAlphaLowerCase(""))
      assert( !isAlphaLowerCase("[]{},."))
      assert( !isAlphaLowerCase("123456"))
      assert(  isAlphaLowerCase("abcdefg"))
      assert( !isAlphaLowerCase("ABCDEFG"))
    }


    it("Is Alpha Numeric") {
      assert( !isAlphaNumeric(null))
      assert( !isAlphaNumeric(""))
      assert( !isAlphaNumeric("[]{},."))
      assert(  isAlphaNumeric("123456"))
      assert(  isAlphaNumeric("abcdefg"))
      assert(  isAlphaNumeric("ABCDEFG"))
    }


    it("Is Numeric") {
      assert( !isNumeric(null))
      assert( !isNumeric(""))
      assert( !isNumeric("[]{},."))
      assert(  isNumeric("123456"))
      assert( !isNumeric("abcdefg"))
      assert( !isNumeric("ABCDEFG"))
    }


    it("Is Url") {
      assert( !isUrl(null))
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


    it("Is Phone US") {
      assert( !isPhoneUS(null))
      assert( !isPhoneUS(""))
      assert( !isPhoneUS("[]{},."))
      assert( !isPhoneUS("123456"))
      assert( !isPhoneUS("abcdefg"))
      assert( !isPhoneUS("ABCDEFG"))
      assert(  isPhoneUS("123-456-7890"))
      assert(  isPhoneUS("1234567890"))
    }


    it("Is ZipCode US") {
      assert( !isZipCodeUS(null))
      assert( !isZipCodeUS(""))
      assert( !isZipCodeUS("[]{},."))
      assert( !isZipCodeUS("123456"))
      assert( !isZipCodeUS("abcdefg"))
      assert( !isZipCodeUS("ABCDEFG"))
      assert(  isZipCodeUS("12345"))
      assert(  isZipCodeUS("01234"))
    }
  }


  describe("Composable: with simple rules") {

    it("all true") {

      // Both true: length 6 && alpha numeric
      assert( allTrue ( buildSimpleRules, "abc123" ) )
    }


    it("all false") {

      // Both false: length 7, && has symbol #
      assert( allFalse( buildSimpleRules, "abc123#" ) )
    }


    it("any true") {

      // 1 true : length 6
      assert( anyTrue ( buildSimpleRules, "[]{}<>" ) )
    }


    it("any false") {

      // 1 false: not alpha-numeric
      assert( anyFalse( buildSimpleRules, "abc12#" ) )
    }
  }


  describe("Composable: with rules returning results") {

    it("all true") {

      // Both true: length 6 && alpha numeric
      val results = collect ( buildRules, "abc123" )
      assert( results.size == 0 )
    }


    it("all false") {

      // Both false: length 7, && has symbol #
      val results = collect( buildRules, "abc123#")
      assert( results.size == 2 )
      assert( results(0).msg == Some(Error_Is_Length))
      assert( results(1).msg == Some(Error_Is_AlphaNumeric))
    }


    it("any true") {

      // 1 true : length 6
      val results = collect ( buildRules, "[]{}<>" )
      assert( results(0).msg == Some(Error_Is_AlphaNumeric ))
    }


    it("any false") {

      // 1 false: not alpha-numeric
      assert( collect( buildRules, "abc12#" )(0).msg == Some(Error_Is_AlphaNumeric))
    }
  }


  class UserValidator extends Validator[User] {

    override def validate(item:User): ValidationResults = {

      val errors = collect (
        Seq (
          () => ValidationFuncsExt.isLength      ( item.password, 10     , Some(new RefField("Email")), "Email must be 6 characters")          ,
          () => ValidationFuncsExt.hasCharsLCase ( item.password, 3      , Some(new RefField("Email")), "Email must have 3 lowercase letters") ,
          () => ValidationFuncsExt.hasCharsUCase ( item.password, 3      , Some(new RefField("Email")), "Email must have 3 uppercase letters") ,
          () => ValidationFuncsExt.hasDigits     ( item.password, 3      , Some(new RefField("Email")), "Email must have 3 digits")            ,
          () => ValidationFuncsExt.isBetween     ( item.age     , 18, 100, None                       , "Age must be between 18 and 100")
        )
      )
      ValidationResults(Option(errors))
    }
  }


  private def buildRules(): Seq[(String) => ValidationResult] = {
    val isLen6   = ValidationFuncsExt.isLength(_:String, 6, None, Error_Is_Length)
    val isAlphaN = ValidationFuncsExt.isAlphaNumeric(_:String, None, Error_Is_AlphaNumeric)
    val rules =  Seq(isLen6, isAlphaN)
    rules
  }


  private def buildSimpleRules(): Seq[(String) => Boolean] = {
    val isLen6   = isLength(_:String, 6)
    val isAlphaN = isAlphaNumeric _
    val rules =  Seq(isLen6, isAlphaN)
    rules
  }
}
