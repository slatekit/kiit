/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.examples

//<doc:import_required>
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.types.*
//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd

//</doc:import_examples>


class Example_SmartStrings : Cmd("results") {

    override fun executeInternal(args: Array<String>?): Result<Any> {
        //<doc:examples>
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
        //</doc:examples>

        return ok()
    }

/*
//<doc:output>
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
  //</doc:output>
  */
}
