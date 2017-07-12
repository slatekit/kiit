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

//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Result
import slatekit.common.conf.ConfFuncs
import slatekit.common.conf.Config
import slatekit.common.db.DbCon
import slatekit.common.encrypt.Encryptor
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd

//</doc:import_examples>


class Example_Config : Cmd("config") {

    override fun executeInternal(args: Array<String>?): Result<Any> {
        //<doc:examples>
        // CASE 1: Load up config from resources directory
        val conf = Config("env.dev.conf")

        // CASE 2: Get typed value: non-nullable
        // NOTE: default value for typed returned if unavailable
        println("bool  : " + conf.getBool("bVal"))
        println("string: " + conf.getString("sVal"))
        println("short : " + conf.getShort("hVal"))
        println("int   : " + conf.getInt("iVal"))
        println("long  : " + conf.getLong("lVal"))
        println("float : " + conf.getFloat("fVal"))
        println("double: " + conf.getDouble("dVal"))

        // CASE 3: Get typed value: nullable e.g. Int?
        println("bool  : " + conf.getBoolOpt("bVal"))
        println("string: " + conf.getStringOpt("sVal"))
        println("short : " + conf.getShortOpt("hVal"))
        println("int   : " + conf.getIntOpt("iVal"))
        println("long  : " + conf.getLongOpt("lVal"))
        println("float : " + conf.getFloatOpt("fVal"))
        println("double: " + conf.getDoubleOpt("dVal"))

        // CASE 4: Get typed value: with default value if unavailable.
        println("bool  : " + conf.getBoolOrElse("bVal", false))
        println("string: " + conf.getStringOrElse("sVal", "abc"))
        println("short : " + conf.getShortOrElse("hVal", 0))
        println("int   : " + conf.getIntOrElse("iVal", 0))
        println("long  : " + conf.getLongOrElse("lVal", 0))
        println("float : " + conf.getFloatOrElse("fVal", 0.0f))
        println("double: " + conf.getDoubleOrElse("dVal", 0.0))

        // CASE 5: Get lists/maps
        println("list  : " + conf.getList("listValInt", Int::class))
        println("map   : " + conf.getMap("mapValInt", String::class, Int::class))
        println()

        // More features such as decryption, file refs and more.
        // ...

        // CASE 6: Get the environment selection ( env, dev, qa ) from conf or default
        val env = conf.env()
        println("${env.name}, ${env.mode.name}, ${env.key}")
        println()


        // CASE 7: Inherit config from another config in resources folder
        // e.g. env.dev.conf ( dev environment ) can inherit from env.conf ( common )
        val confs1 = ConfFuncs.loadWithFallbackConfig("env.dev.conf", "env.conf")
        val dbConInherited = confs1.dbCon()
        printDbCon("db con - inherited : ", dbConInherited)


        // CASE 8: Get overriden inherited config settings
        // e.g. env.loc.conf ( local environment ) overrides settings inherited from env.conf
        val confs2 = ConfFuncs.loadWithFallbackConfig("env.loc.conf", "env.conf")
        val dbConOverride = confs2.dbCon()
        printDbCon("db con - override : ", dbConOverride)


        // CASE 9: Multiple db settings, get 1 using a prefix
        // e.g. env.qa.conf ( qa environment ) with 2 db settings get one with "qa2" prefix.
        val confs3 = ConfFuncs.loadWithFallbackConfig("env.qa1.conf", "env.conf")
        val dbConMulti = confs3.dbCon("qa1")
        printDbCon("db con - multiple : ", dbConMulti)


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
        val dbConFile = confs4.dbCon(prefix = "db")
        printDbCon("db con - file ref: ", dbConFile)


        // CASE 10: Decryp encrypted strings in the config file
        // e.g.
        // db.user = "@{decrypt('8r4AbhQyvlzSeWnKsamowA')}"
        val encryptor = Encryptor("wejklhviuxywehjk", "3214maslkdf03292")
        val confs5 = ConfFuncs.loadWithFallbackConfig("env.qa1.conf", "env.conf", enc = encryptor)
        println("db user decrypted : " + confs5.getString("db.user"))
        println("db pswd decrypted : " + confs5.getString("db.pswd"))
        println()
        //</doc:examples>
        return ok()
    }


    private fun printDbCon(desc: String, con: DbCon?): Unit {
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
