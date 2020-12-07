/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2016 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.examples

//<doc:import_required>
import slatekit.common.validations.Validations
//</doc:import_required>

//<doc:import_examples>

import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.validations.ValidationFuncs.isEmpty
import slatekit.common.validations.ValidationFuncs.isNotEmpty
import slatekit.common.validations.ValidationFuncs.isLength
import slatekit.common.validations.ValidationFuncs.isMinLength
import slatekit.common.validations.ValidationFuncs.isMaxLength
import slatekit.common.validations.ValidationFuncs.isMinValue
import slatekit.common.validations.ValidationFuncs.isMaxValue
import slatekit.common.validations.ValidationFuncs.hasDigits
import slatekit.common.validations.ValidationFuncs.hasCharsLCase
import slatekit.common.validations.ValidationFuncs.hasCharsUCase
import slatekit.common.validations.ValidationFuncs.hasSymbols
import slatekit.common.validations.ValidationFuncs.isAlpha
import slatekit.common.validations.ValidationFuncs.isAlphaLowerCase
import slatekit.common.validations.ValidationFuncs.isAlphaNumeric
import slatekit.common.validations.ValidationFuncs.isAlphaUpperCase
import slatekit.common.validations.ValidationFuncs.isEmail
import slatekit.common.validations.ValidationFuncs.isNumeric
import slatekit.common.validations.ValidationFuncs.isPhoneUS
import slatekit.common.validations.ValidationFuncs.isUrl
import slatekit.common.validations.ValidationFuncs.isZipCodeUS


//</doc:import_examples>

/**
  * Created by kreddy on 10/21/2016.
  */
class Example_Validation : Command("validation") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    showSimple()
    showSimpleRegEx()
    showValidationResult()
    showErrorCollection()
    return Success("")
  }


  //<doc:examples>
  // CASE 1: Simple true/false checks
  fun showSimple() {

    println("CASE 1: Simple true/false checks")
    println( isEmpty      ("")              )
    println( isNotEmpty   ("123")           )
    println( isLength     ("123", 3)        )
    println( isMinLength  ("123", 3)        )
    println( isMaxLength  ("12" , 3)        )
    println( isMinValue   (100, 100)        )
    println( isMaxValue   (99 , 100)        )
    println( hasDigits    ("a1b2c3"  , 3)   )
    println( hasCharsLCase("a1b2c3"  , 3)   )
    println( hasCharsUCase("A1B2C3"  , 3)   )
    println( hasSymbols   ("A@B%C^"  , 3)   )
    println()
  }


  // CASE 2: Simple RegEx based checks returning true/false
  fun showSimpleRegEx() {

    println("CASE 2: Simple RegEx based checks returning true/false")
    println( isEmail         ("wonderwoman@amazonian.com"))
    println( isUrl           ("http://slatekit.com") )
    println( isAlpha         ("abcDEF")      )
    println( isAlphaUpperCase("ABCDEFG")     )
    println( isAlphaLowerCase("abcdefg")     )
    println( isAlphaNumeric  ("abCD12")      )
    println( isNumeric       ("123456")      )
    println( isPhoneUS       ("123-456-7890"))
    println( isZipCodeUS     ("12345")       )
    println()
  }


  // CASE 3: Same checks above but these return a ValidationResult
  // which contains success(true/false), message, reference, and status code
  // You can supply a reference to a field/position refer to common\Reference.kt
  fun showValidationResult() {

    println("CASE 3: Same checks above but these return a ValidationResult")
    println( Validations.isEmpty       (""      ,     "Email is required"   ))
    println( Validations.isAlphaNumeric("abCD12",     "Password is invalid" ))
    println( Validations.isZipCodeUS   ("12345" ,     "ZipCode is required" ))
    println( Validations.isMinLength   ("12"    , 3, "Min 3 chars required"))
    println()
  }


  // CASE 4: Collect errors via thunks(0 parameter functions)
  fun showErrorCollection() {

    val password = "abc123XYZ"

    println("CASE 4: Collect errors via thunks(0 parameter functions)")
    val errors = listOf (
        { Validations.isLength      ( password,   9 , "Email must be 9 characters")          } ,
        { Validations.hasCharsLCase ( password, 3 , "Email must have 3 lowercase letters") } ,
        { Validations.hasCharsUCase ( password, 3 , "Email must have 3 uppercase letters") } ,
        { Validations.hasDigits     ( password, 3 , "Email must have 3 digits")            }
      ).map    { rule -> rule() }
       .filter { result -> !result.success }
       .toList()

    errors.forEach{ err -> println( err ) }
    println()
  }

  // Case 5: Custom validator object
  data class User(val email:String, val password:String) { }


  //</doc:examples>
}
