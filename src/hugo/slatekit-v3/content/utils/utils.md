
# Utils

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Various utilities available in the Slate library</td>
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
      <td>slatekit.common.utils</td>
    </tr>
    <tr>
      <td><strong>source core</strong></td>
      <td>slatekit.common.console.ConsoleWriter.kt</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/utils" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/utils</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Utils.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Utils.kt</a></td>
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
import slatekit.common.utils.Require.requireOneOf
import slatekit.common.utils.Require.requireText
import slatekit.common.utils.Require.requireValidIndex
import slatekit.common.*



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.db.DbConString
import slatekit.common.info.ApiKey
import slatekit.common.info.ApiLogin
import slatekit.results.Try
import slatekit.results.Success




{{< /highlight >}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}

## Usage
{{< highlight kotlin >}}


    // Miscellaneous utilities.

    // CASE 1: Api Credentials
    // The ApiCredentials class provides a convenient container for most fields required to
    // represent the access credentials some API such as AWS ( Amazon Web Services ) or Azure.
    val awsS3    = ApiLogin("aws-s3", "ABCDEFG", "123456", "dev", "user-profile")
    val twilio   = ApiLogin("1-234-567-8901", "ABCEDEFG", "123456", "dev", "sms")
    val sendgrid = ApiLogin("support@mystartup.com", "mystartup", "123456789", "dev", "emails")

    // CASE 2: Api Keys
    val devKey = ApiKey("dev1", "B8779D64-6104-4244-88B6-F81B4D2AAF5B", "dev", mapOf())
    val qaKey  = ApiKey("qa1", "F01718FF-0AF5-43C2-84D7-D1E2B4234644", "qa", mapOf())

    // CASE 3: Guards ( Exceptions are discouraged in favor
    // of functional error handling, however, there are times where
    // a guard like approach to inputs is preferable
    requireText("slate-kit", "Name must be supplied" )
    requireOneOf( "scala", listOf("scala", "go"), "Name not valid")
    requireValidIndex( 3, 4, "Index is must be 0 <= index <= 4")

    // CASE 4: Db connection
    val db = DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/app1", "db1", "1245689")

    // CASE 5: Interpret URI represetning file locations
    // - user dir: user://{folder} ( user home directory for os  )
    // - temp dir: temp://{folder} ( temp files directory for os )
    // - file dir: file://{path}   ( absolution file location    )
    // This yeilds c:/users/{user1}/myapp1
    val path1 = Uris.interpret("user://myapp1")
    println(path1)

    

{{< /highlight >}}


