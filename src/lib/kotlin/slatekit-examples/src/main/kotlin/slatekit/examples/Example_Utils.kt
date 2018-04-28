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
import slatekit.common.Require.requireOneOf
import slatekit.common.Require.requireText
import slatekit.common.Require.requireValidIndex
import slatekit.common.*

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.db.DbConString
//</doc:import_examples>


class Example_Utils : Cmd("utils") {

  override fun executeInternal(args: Array<String>?) : ResultEx<Any>
  {
    //<doc:examples>
    // Miscellaneous utilities.

    // CASE 1: Api Credentials
    // The ApiCredentials class provides a convenient container for most fields required to
    // represent the access credentials some API such as AWS ( Amazon Web Services ) or Azure.
    val awsS3    = ApiLogin("aws-s3", "ABCDEFG", "123456", "dev", "user-profile")
    val twilio   = ApiLogin("1-234-567-8901", "ABCEDEFG", "123456", "dev", "sms")
    val sendgrid = ApiLogin("support@mystartup.com", "mystartup", "123456789", "dev", "emails")

    // CASE 2: Api Keys
    val devKey = ApiKey("dev1", "B8779D64-6104-4244-88B6-F81B4D2AAF5B", "dev", mapOf())
    val qaKey  = ApiKey("qa1" , "F01718FF-0AF5-43C2-84D7-D1E2B4234644", "qa", mapOf())

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

    // CASE 6: BoolMessage
    // combined bool / string instead of a tuple
    val result = BoolMessage(true, "successfuly created")
    //</doc:examples>
    return Success("")
  }
}

