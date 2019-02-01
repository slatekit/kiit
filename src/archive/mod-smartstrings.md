---
layout: start_page_mods_utils
title: module SmartStrings
permalink: /kotlin-mod-smartstrings
---

# SmartStrings

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A way to store, validate and describe strongly typed and formatted strings | 
| **date**| 2018-03-29 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.types  |
| **source core** | slatekit.common.types.Email.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_SmartStrings.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_SmartStrings.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.types.*


// optional 
import slatekit.core.cmds.Cmd



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


        // Smart strings provide a way to store, validate and describe
        // a strongly typed/formatted string. This concept is called
        // a smart string and is designed to be used specifically at
        // application boundaries ( such as API endpoints ).
        // There are a few smart strings provided out of the box such as
        // 1. Email
        // 2. PhoneUS
        // 3. SSN
        // 4. ZipCode
        val email = Email("batman@gotham.com", true)

        // Case 1: Get the actual value
        println("value: " + email.text)

        // Case 2: Check if valid
        println("valid?: " + email.isValid)

        // Case 3: Get the name / type of smart string
        println("name: " + email.name)

        // Case 4: Description
        println("desc: " + email.desc)

        // Case 4: Examples of valid string
        println("examples: " + email.examples)

        // Case 5: Format of the string
        println("format(s): " + email.formats)

        // Case 6: The regular expression representing string
        println("reg ex: " + email.expressions)

        // Case 7: Get the first example
        println("example: " + email.example())

        // Case 8: Get the Email specific values ( domain )
        println("domain?: " + email.domain())
        println("dashed?: " + email.isDashed())

        // Case 9: Other smart strings
        val phone = PhoneUS("212-456-7890")
        

```


## Output

```bat
  value: batman@gotham.com
  valid?: true
  name: Email
  desc: Email Address
  examples: [user@abc.com]
  format(s): [xxxx@xxxxxxx]
  reg ex: [([\w\$\.\-_]+)@([\w\.]+)]
  example: user@abc.com
  domain?: gotham.com
  dashed?: false

```
  