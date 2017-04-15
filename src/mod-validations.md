---
layout: start_page_mods_utils
title: module Validations
permalink: /mod-validations
---

# Validations

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A set of validation related components, simple validation checks, RegEx checks, error collection and custom validators | 
| **date**| 2017-04-12T22:59:15.365 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.validation  |
| **source core** | slate.common.validation.ValidationFuncs.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/validation](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/validation)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Validation.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Validation.scala) |
| **depends on** |   |

## Import
```scala 
// required 

import slate.common.results.ResultSupportIn
import slate.common.validations.{Validator, ValidationResults}
import slate.common.validations.Validations._
import slate.common.{Result, RefField}


// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


  // CASE 1: Simple true/false checks
  def showSimple():Unit = {

    import slate.common.validations.ValidationFuncs._

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
    println( hasSymbols   ("A$B%C^"  , 3)   )
    println()
  }

  // CASE 2: Simple RegEx based checks returning true/false
  def showSimpleRegEx():Unit = {

    import slate.common.validations.ValidationFuncs._

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
  def showValidationResult():Unit = {

    import slate.common.validations.ValidationFuncsExt._

    println("CASE 3: Same checks above but these return a ValidationResult")
    println( isEmpty       (""      ,    Some(new RefField("Email"   )) , "Email is required"   ))
    println( isAlphaNumeric("abCD12",    Some(new RefField("Password")) , "Password is invalid" ))
    println( isZipCodeUS   ("12345" ,    Some(new RefField("ZipCode" )) , "ZipCode is required" ))
    println( isMinLength   ("12"    , 3, Some(new RefField("Name"    )) , "Min 3 chars required"))
    println()
  }

  // CASE 4: Collect errors via thunks(0 parameter functions)
  def showErrorCollection():Unit = {

    import slate.common.validations.ValidationFuncsExt._

    val password = "abc123XYZ"
    val reference = Some(new RefField("Email"))

    println("CASE 4: Collect errors via thunks(0 parameter functions)")
    val errors = collect (
      Seq (
        () => isLength      ( password, 9 , reference, "Email must be 9 characters")          ,
        () => hasCharsLCase ( password, 3 , reference, "Email must have 3 lowercase letters") ,
        () => hasCharsUCase ( password, 3 , reference, "Email must have 3 uppercase letters") ,
        () => hasDigits     ( password, 3 , reference, "Email must have 3 digits")
      )
    )
    errors.foreach( err => println( err ) )
    println()
  }

  // Case 5: Custom validator object
  case class User(email:String, password:String) { }

  // Extend from Validation[T]
  class UserValidator extends Validator[User] {

    // Implement your validation here and collect errors
    // The validation results represents a contain for success, message, code, and errors.
    override def validate(item:User): ValidationResults = {

      import slate.common.validations.ValidationFuncsExt._

      val password = "abc123XYZ"
      val reference = Some(new RefField("Email"))

      println("Case 5: Custom validator object")
      val errors = collect (
        Seq (
          () => isLength      ( password, 9 , reference, "Email must be 9 characters")          ,
          () => hasCharsLCase ( password, 3 , reference, "Email must have 3 lowercase letters") ,
          () => hasCharsUCase ( password, 3 , reference, "Email must have 3 uppercase letters") ,
          () => hasDigits     ( password, 3 , reference, "Email must have 3 digits")
        )
      )
      ValidationResults(Option(errors))
    }
  }
  

```

