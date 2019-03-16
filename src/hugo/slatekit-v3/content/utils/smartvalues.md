
# SmartValues

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>A way to store, validate and describe strongly typed and formatted strings</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-15</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.9</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.common.smartvalues</td>
    </tr>
    <tr>
      <td><strong>source core</strong></td>
      <td>slatekit.common.smartvalues.Email.kt</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/smartvalues" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/smartvalues</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_SmartValues.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_SmartValues.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td></td>
    </tr>
  </tbody>
</table>



## Import
{{< highlight kotlin >}}


// required 
import slatekit.common.smartvalues.Email
import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.smartvalues.*


// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Outcome




{{< /highlight >}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}

## Usage
{{< highlight kotlin >}}


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
        

{{< /highlight >}}



## Output

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
  