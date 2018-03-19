---
layout: start_page_mods_utils
title: module Validations
permalink: /kotlin-mod-validations
---

# Validations

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A set of validation related components, simple validation checks, RegEx checks, error collection and custom validators | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.validation  |
| **source core** | slatekit.common.validation.ValidationFuncs.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Validation.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Validation.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.RefField
import slatekit.common.validations.Validator
import slatekit.common.validations.ValidationFuncsExt
import slatekit.common.validations.ValidationResults
import slatekit.common.validations.Validations.collect


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
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



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


  // CASE 1: Simple true/false checks
  fun showSimple():Unit {

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
  fun showSimpleRegEx():Unit {

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
  // You can supply a reference to a field/position refer to common\Reference.scala
  fun showValidationResult():Unit {

    println("CASE 3: Same checks above but these return a ValidationResult")
    println( ValidationFuncsExt.isEmpty       (""      ,    RefField("Email"   ) , "Email is required"   ))
    println( ValidationFuncsExt.isAlphaNumeric("abCD12",    RefField("Password") , "Password is invalid" ))
    println( ValidationFuncsExt.isZipCodeUS   ("12345" ,    RefField("ZipCode" ) , "ZipCode is required" ))
    println( ValidationFuncsExt.isMinLength   ("12"    , 3, RefField("Name"    ) , "Min 3 chars required"))
    println()
  }


  // CASE 4: Collect errors via thunks(0 parameter functions)
  fun showErrorCollection():Unit {

    val password = "abc123XYZ"
    val reference = RefField("Email")

    println("CASE 4: Collect errors via thunks(0 parameter functions)")
    val errors = collect ( listOf (
        { -> ValidationFuncsExt.isLength      ( password, 9 , reference, "Email must be 9 characters")          } ,
        { -> ValidationFuncsExt.hasCharsLCase ( password, 3 , reference, "Email must have 3 lowercase letters") } ,
        { -> ValidationFuncsExt.hasCharsUCase ( password, 3 , reference, "Email must have 3 uppercase letters") } ,
        { -> ValidationFuncsExt.hasDigits     ( password, 3 , reference, "Email must have 3 digits")            }
      )
    )
    errors.forEach{ err -> println( err ) }
    println()
  }

  // Case 5: Custom validator object
  data class User(val email:String, val password:String) { }

  // Extend from Validation[T]
  class UserValidator : Validator<User>() {

    // Implement your validation here and collect errors
    // The validation results represents a contain for success, message, code, and errors.
    override fun validate(item:User): ValidationResults {

      val password = "abc123XYZ"
      val reference = RefField("Email")

      println("Case 5: Custom validator object")
      val errors = collect ( listOf(
          { -> ValidationFuncsExt.isLength      ( password, 9 , reference, "Email must be 9 characters")          },
          { -> ValidationFuncsExt.hasCharsLCase ( password, 3 , reference, "Email must have 3 lowercase letters") },
          { -> ValidationFuncsExt.hasCharsUCase ( password, 3 , reference, "Email must have 3 uppercase letters") },
          { -> ValidationFuncsExt.hasDigits     ( password, 3 , reference, "Email must have 3 digits")            }
        )
      )
      return ValidationResults.build(errors)
    }
  }
  

```

