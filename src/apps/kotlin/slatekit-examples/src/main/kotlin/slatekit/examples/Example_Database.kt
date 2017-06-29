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
import slatekit.common.db.*
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.db.DbLookup.DbLookupCompanion.namedDbs
import slatekit.common.mapper.Mapper
import slatekit.entities.core.EntityMapper

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.conf.Config
import slatekit.common.results.ResultFuncs.ok
import slatekit.examples.common.User

//</doc:import_examples>

class Example_Database : Cmd("db") {

  override fun executeInternal(args: Array<String>?) : Result<Any>
  {
    //<doc:examples>
    // These examples show 2 parts of the database component:
    // 1. Database connection registration
    // 2. Database reading/writing

    // Manual creation via class
    val dbs1 = DbLookup()
    showResult( dbs1.default() )

    // CASE 2: Default database connection using credentials from users folder with content:
    // e.g. on windows: C:\Users\kv\slatekit\conf\db.txt
    //
    // driver:com.mysql.jdbc.Driver
    // url:jdbc:mysql://localhost/World
    // user:root
    // password:123abc
    val dbs2 = defaultDb( DbUtils.loadFromUserFolder(".slatekit", "db_default.txt"))
    showResult( dbs2.default() )

    // CASE 3: Default database connection explicitly
    val dbs3 = defaultDb(
      DbConString (
        "com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost/default",
        "root",
        "abcdefghi"
      )
    )
    showResult( dbs3.default() )

    // CASE 4: Register connection and link to a key "user_db" using credentials from user folder
    val dbs4 = namedDbs( listOf(Pair("users", DbUtils.loadFromUserFolder(".slate", "db_default.txt"))))
    showResult( dbs4.named("users") )

    // CASE 5: Register multiple connections by api ( "users", "files" )
    val dbs5 = namedDbs(listOf(
      Pair(
        "users", DbConString (
          "com.mysql.jdbc.Driver",
          "jdbc:mysql://localhost/users",
          "root",
          "abcdefghi"
        )
      ),
      Pair(
        "files", DbConString (
          "com.mysql.jdbc.Driver",
          "jdbc:mysql://localhost/files",
          "root",
          "abcdefghi"
        )
      )
    ))
    showResult( dbs5.named("users") )
    showResult( dbs5.named("fiels") )



    // CASE 7: Now create the database class and start using some basic methods
    val db = Db(Config().dbCon("db"))

    // CASE 8: Open the database
    db.open()

    // CASE 8: Get scalar int value
    val total1 = db.getScalarInt("select count(*) from City")

    // CASE 9: Execute a sql insert
    val id1 = db.insert("insert into `city`(`api`) values( 'ny' )" )

    // CASE 10: Execute a sql insert using
    val id2 = db.insert("insert into `city`(`api`) values( 'ny' )" )

    // CASE 10: Execute a sql update
    val total2 = db.update("update `city` set `api` = 'ny' where id = 2")

    // CASE 11: Map a record to an model using the mapper component
    val model = Mapper.loadSchema(User::class)
    val mapper = EntityMapper(model)
    val item1 = db.mapOne<User>("select * from `city` where id = 1", mapper)
    println( item1 )

    // CASE 12: Map multiple records
    val items = db.mapMany<User>("select * from `city` where id < 5", mapper)
    println( items )

    //</doc:examples>
    return ok()
  }


  fun showResult(con: DbCon?):Unit {
    println(con)
  }
}
