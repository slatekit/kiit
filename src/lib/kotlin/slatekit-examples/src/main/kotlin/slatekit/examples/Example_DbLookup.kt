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

//</doc:import_required>

//<doc:import_examples>
import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbCon
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.Companion.defaultDb
import slatekit.common.db.DbLookup.Companion.namedDbs
import slatekit.core.cmds.Command
import slatekit.core.cmds.CommandRequest

//</doc:import_examples>

class Example_DbLookup : Command("db") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>
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
        val dbs2: DbLookup = defaultDb(ConfFuncs.readDbCon("user://.slatekit/conf/db.txt")!!)
        showResult(dbs2.default())


        // CASE 4: Register connection and link to a key "user_db" using credentials from user folder
        val dbs4: DbLookup = namedDbs(
            listOf(Pair("users", ConfFuncs.readDbCon("user://.slatekit/conf/db_default.txt")!!))
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
        //</doc:examples>
        return Success("")
    }


    fun showResult(con: DbCon?): Unit {
        println(con)
    }
}
