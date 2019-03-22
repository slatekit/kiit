
# Config

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Thin wrapper over typesafe config with decryption support, uri loading, and mapping of database connections and api keys</td>
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
      <td>slatekit.common.conf</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-common</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/conf" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/conf</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Config.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Config.kt</a></td>
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
import slatekit.common.conf.Config
import slatekit.common.db.DbCon
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
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
        println("bool  : " + conf.getBoolOrNull("bVal"))
        println("string: " + conf.getStringOrNull("sVal"))
        println("short : " + conf.getShortOrNull("hVal"))
        println("int   : " + conf.getIntOrNull("iVal"))
        println("long  : " + conf.getLongOrNull("lVal"))
        println("float : " + conf.getFloatOrNull("fVal"))
        println("double: " + conf.getDoubleOrNull("dVal"))

        // CASE 4: Get typed value: with default value if unavailable.
        println("bool  : " + conf.getBoolOrElse("bVal", false))
        println("string: " + conf.getStringOrElse("sVal", "abc"))
        println("short : " + conf.getShortOrElse("hVal", 0))
        println("int   : " + conf.getIntOrElse("iVal", 0))
        println("long  : " + conf.getLongOrElse("lVal", 0))
        println("float : " + conf.getFloatOrElse("fVal", 0.0f))
        println("double: " + conf.getDoubleOrElse("dVal", 0.0))

        // CASE 5: Get lists/maps
        println("list  : " + conf.getList("listValInt", Int::class.java))
        println("map   : " + conf.getMap("mapValInt", String::class.java, Int::class.java))
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
        val encryptor = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8)
        val confs5 = ConfFuncs.loadWithFallbackConfig("env.qa1.conf", "env.conf", enc = encryptor)
        println("db user decrypted : " + confs5.getString("db.user"))
        println("db pswd decrypted : " + confs5.getString("db.pswd"))
        println()
        

{{< /highlight >}}
{{% break %}}


## Output

{{< highlight bat >}}
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
{{< /highlight >}}
