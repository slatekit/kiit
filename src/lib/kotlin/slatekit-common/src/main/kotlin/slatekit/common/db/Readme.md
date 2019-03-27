
# DbLookup

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Database access utilty to query and manage data using JDBC for MySql. Other database support coming later.</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.common.db</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-common</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/db" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/db</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_DbLookup.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_DbLookup.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 



// optional 
import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbCon
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.Companion.defaultDb
import slatekit.common.db.DbLookup.Companion.namedDbs
import slatekit.core.cmds.Cmd




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


        // These examples just shows the database connection registration
        // There is separate Db component in slatekit.common.db.Db
        // that handles db functions: query, insert, update, delete, scalar

        // CASE 1: The DbLookup component holds all the database
        // connections associated with a name. You can also set
        // the default database. In this case, no db connections
        // have been registered, so it will be empty.
        val dbs1 = DbLookup()
        showResult(dbs1.default())


        // CASE 2: Create the DbLookup using just 1 explicit connection
        val dbs3: DbLookup = defaultDb(
                DbConString(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://localhost/default",
                        "root",
                        "abcdefghi"
                )
        )
        showResult(dbs3.default())


        // CASE 3: Create the DbLookup using just 1 connection from a file in the user directory
        // e.g. on windows: C:\Users\kv\slatekit\conf\db.txt
        //
        // NOTES:
        // 1. This is much safer and the recommended approach to storing DB connections.
        // 2. You should also encrypt the username/password
        //
        // driver:com.mysql.jdbc.Driver
        // url:jdbc:mysql://localhost/World
        // user:root
        // password:123abc
        val dbs2: DbLookup = defaultDb(ConfFuncs.readDbCon("user://.slatekit/conf/db.txt"))
        showResult(dbs2.default())


        // CASE 4: Register connection and link to a key "user_db" using credentials from user folder
        val dbs4: DbLookup = namedDbs(
            listOf(Pair("users", ConfFuncs.readDbCon("user://.slatekit/conf/db_default.txt"))
        )
        showResult(dbs4.named("users"))

        // CASE 5: Register multiple connections by api ( "users", "files" )
        val dbs5 = namedDbs(listOf(
            Pair(
                "users", DbConString(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost/users",
                    "root",
                    "abcdefghi"
                    )
            ),
            Pair(
                "files", DbConString(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost/files",
                    "root",
                    "abcdefghi"
                )
            )
        ))
        showResult(dbs5.named("users"))
        showResult(dbs5.named("files"))
        

{{< /highlight >}}
{{% break %}}

