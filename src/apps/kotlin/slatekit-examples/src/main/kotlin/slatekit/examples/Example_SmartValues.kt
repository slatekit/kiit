/**
<slate_header>
author: Kishore Reddy
url: www.github.com/code-helix/slatekit
copyright: 2015 Kishore Reddy
license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
desc: A tool-kit, utility library and server-backend
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.examples

//<doc:import_required>
import slatekit.common.smartvalues.Email
import slatekit.results.Try
import slatekit.results.Success
//</doc:import_required>

//<doc:import_examples>


import slatekit.results.Outcome

//</doc:import_examples>


class Example_SmartValues : Command("results") {

    override fun execute(request: CommandRequest): Try<Any> {
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
        val email = Email.of("batman@gotham.com")

        // Case 1: Get the actual value
        println("value: " + email.value)

        // Case 2: Check if valid
        println("valid?: " + Email.isValid("batman@gotham.com"))

        // Case 3: Get the name / type of smart string
        println("name: " + email.meta.name)

        // Case 4: Description
        println("desc: " + email.meta.desc)

        // Case 4: Examples of valid string
        println("examples: " + email.meta.examples)

        // Case 5: Format of the string
        println("format(s): " + email.meta.formats)

        // Case 6: The regular expression representing string
        println("reg ex: " + email.meta.expressions)

        // Case 7: Get the first example
        println("example: " + email.meta.example)

        // Case 9: Using slatekit.result types
        // Try<Email> = Result<Email,Exception>
        val email2:Try<Email> = Email.attempt("batman@gotham.com")

        // Outcome<Email> = Result<Email,Err>
        val email3:Outcome<Email> = Email.outcome("batman@gotham.com")
        //</doc:examples>

        return Success("")
    }

/*
//<doc:output>
{{< highlight yaml >}}
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

{{< /highlight >}}
  //</doc:output>
  */
}
