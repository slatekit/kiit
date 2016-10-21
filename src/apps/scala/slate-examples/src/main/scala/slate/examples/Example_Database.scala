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


package slate.examples

//<doc:import_required>
import slate.common.databases.{DbConfig, DbLookup, Db, DbConString}
import slate.common.results.ResultSupportIn
import slate.core.common.Conf
import slate.entities.core.EntityMapper
import slate.examples.common.User

//</doc:import_required>
//<doc:import_examples>
import slate.core.cmds.Cmd
import slate.common.{Result}
import scala.reflect.runtime.universe.{typeOf}
//</doc:import_examples>

class Example_Database extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // CASE 1: Default database connection using credentials from users folder with content:
    //
    // e.g. on windows: C:\Users\kv\slatekit\conf\db.txt
    //
    // driver:com.mysql.jdbc.Driver
    // url:jdbc:mysql://localhost/World
    // user:root
    // password:123abc
    DbLookup.setDefault( Some(DbConfig.loadFromUserFolder(".slate", "db_default.txt") ))

    // CASE 2: Default database connection explicitly ( not recommended - store config files in your user folder )
    DbLookup.setDefault("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/default", "root", "abcdefghi")
    showResult( DbLookup.getDefault )

    // CASE 3: Register connection and link to a key "user_db" using credentials from user folder
    DbLookup.set("user_db", DbConfig.loadFromUserFolder(".slate", "db_default.txt") )
    showResult( DbLookup.getByKey("user_db") )

    // CASE 4: Register connection and link to a key "user_db" explicitly
    // ( not recommended - store config files in your user folder )
    DbLookup.set("user_db", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/user_db", "root", "abcdefghi")
    showResult( DbLookup.getByKey("user_db") )

    // CASE 5: Register connection as a shard and link to a group
    DbLookup.shard("group1", "shard1", DbConfig.loadFromUserFolder(".slate", "db_default.txt") )
    DbLookup.shard("group1", "shard2", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")
    showResult( DbLookup.getShard("group1", "shard1") )
    showResult( DbLookup.getShard("group1", "shard2") )

    // CASE 6: Now create the database class and start using some basic methods
    val db = new Db(new Conf().dbCon("db").get)

    // CASE 7: Open the database
    db.open()

    // CASE 8: Get scalar int value
    val total1 = db.getScalarInt("select count(*) from City")

    // CASE 9: Execute a sql insert
    val id1 = db.executeInsertGetId("insert into `city`(`name`) values( 'ny' )" )

    // CASE 10: Execute a sql insert using
    val id2 = db.executeInsertGetId("insert into `city`(`name`) values( 'ny' )" )

    // CASE 10: Execute a sql update
    val total2 = db.executeUpdate("update `city` set `name` = 'ny' where id = 2")

    // CASE 11: Map a record to an model using the mapper component
    val mapper = new EntityMapper(null)
    mapper.loadSchema(new User(), typeOf[User])
    val model = db.mapOne("select * from `city` where id = 1", mapper)
    println( model )

    // CASE 12: Map multiple records
    val models = db.mapMany("select * from `city` where id < 5", mapper)
    println( models )

    //</doc:examples>
    ok()
  }


  def showResult(con: DbConString):Unit =
  {
    println(con)
  }
}
