/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.examples

//<doc:import_required>
import slatekit.common.conf.Config

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbCon
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Dev
import slatekit.common.results.ResultFuncs.ok

//</doc:import_examples>


class Example_Config : Cmd("config") {

  override fun executeInternal(args: Array<String>?) : Result<Any>
  {
    //<doc:examples>
    // CASE 1: Load up config from application.conf in resources
    val conf = Config()
    println( "env.api: " + conf.getString("env.api") )
    println( "env.region: " + conf.getStringOrElse("env.region", "usa") )
    println( "db.enabled: " + conf.getBool("db.enabled") )
    println()


    // CASE 2: Get the environment selection ( env, dev, qa ) from conf or default
    val env = conf.env()
    println( "${env.name}, ${env.mode.name}, ${env.key}")
    println()


    // CASE 3: Inherit config from another config in resources folder
    // e.g. env.dev.conf ( dev environment ) can inherit from env.conf ( common )
    val confs1 = ConfFuncs.loadWithFallbackConfig("env.dev.conf", "env.conf")
    val dbConInherited = confs1.dbCon()
    printDbCon ( "db con - inherited : ", dbConInherited )


    // CASE 4: Override inherited config settings
    // e.g. env.loc.conf ( local environment ) overrides settings inherited from env.conf
    val confs2 = ConfFuncs.loadWithFallbackConfig("env.loc.conf", "env.conf")
    val dbConOverride = confs2.dbCon()
    printDbCon ( "db con - override : ", dbConOverride )


    // CASE 5: Multiple db settings, get 1 using a prefix
    // e.g. env.qa.conf ( qa environment ) with 2 db settings get one with "qa2" prefix.
    val confs3 = ConfFuncs.loadWithFallbackConfig("env.qa1.conf", "env.conf")
    val dbConMulti = confs3.dbCon("qa1")
    printDbCon ( "db con - multiple : ", dbConMulti )


    // CASE 6: File from user directory:
    // You can refer to a file path using a uri syntax:
    //
    // SYNTAX:
    // - "jars://"  refer to resources directory in the jar.
    // - "user://"  refer to user.home directory.
    // - "file://"  refer to an explicit path to the file
    // - "file://"  refer to a relative path to the file from working directory

    // EXAMPLES:
    // - jar://env.qa.conf
    // - user://slatekit/conf/env.qa.conf
    // - file://c:/slatekit/system/slate.shell/conf/env.qa.conf
    // - file://./conf/env.qa.conf
    //
    // CONFIG
    //
    // db {
    //   location: "user://slatekit/conf/db.conf"
    // }
    val confs4 = ConfFuncs.loadWithFallbackConfig("env.pro.conf", "env.conf")
    val dbConFile = confs4.dbCon( prefix = "db")
    printDbCon ( "db con - file ref: ", dbConFile )


    // CASE 7: Decryp encrypted strings in the config file
    // e.g.
    // db.user = "@{decrypt('8r4AbhQyvlzSeWnKsamowA')}"
    val encryptor = Encryptor("wejklhviuxywehjk", "3214maslkdf03292")
    val confs5 = ConfFuncs.loadWithFallbackConfig("env.qa1.conf", "env.conf", enc = encryptor)
    println ( "db user decrypted : " + confs5.getString("db.user") )
    println ( "db pswd decrypted : " + confs5.getString("db.pswd") )
    println()
    //</doc:examples>
    return ok()
  }


  private fun printDbCon(desc:String, con: DbCon?):Unit {
    con?.let { c ->
      println(desc)
      println("driver: " + c.driver)
      println("url   : " + c.url)
      println("user  : " + c.user)
      println("pswd  : " + c.password)
      println()
    }
  }


  /*
  //<doc:output>
```bat
 env.api: lc1
  db.enabled: true

  lc1, dev, dev : lc1


  db con - inherited :
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/db1
  user  : root
  pswd  : 123456789


  db con - override :
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/db1
  user  : root
  pswd  : 123456789


  db con - multiple :
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/db1
  user  : root
  pswd  : 123456789


  db con - file ref:
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/test1
  user  : root
  pswd  : t$123456789


  db user decrypted : root
  db pswd decrypted : 123456789
```
  //</doc:output>
  */
}
