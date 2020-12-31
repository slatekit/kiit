/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.common

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.info.ApiLogin
import slatekit.common.DateTimes
import slatekit.common.conf.Confs
import slatekit.common.conf.Config
import slatekit.common.conf.MapSettings
import slatekit.common.envs.EnvMode
import slatekit.common.ext.zoned
import slatekit.common.ids.UPID
import slatekit.common.ids.UPIDs
import slatekit.meta.map
import test.TestApp
import test.setup.Movie
import test.setup.MyEncryptor
import java.util.*
import java.io.FileInputStream



/**
 * Created by kishorereddy on 6/4/17.
 */
class ConfigTests {
    val app = TestApp::class.java

    fun load(): Properties {
        val file = this.javaClass.getResource("/" + Confs.CONFIG_DEFAULT_PROPERTIES).file
        val input = FileInputStream(file)
        val conf = Properties()
        conf.load(input)
        return conf
    }


    @Test fun test_props(){
        val conf = load()
        println( conf.getProperty("log.level"))
        println( conf.getProperty("log.file"))
        println("done")
    }


    @Test fun test_basic() {
        val conf = Config.of(app, Confs.CONFIG_DEFAULT_PROPERTIES)
        Assert.assertTrue(conf.getInt("test_int") == 1)
        Assert.assertTrue(conf.getBool("test_bool"))
        Assert.assertTrue(conf.getString("test_text") == "abc")
        Assert.assertTrue(conf.getLong("test_long") == 10L)
        Assert.assertTrue(conf.getDouble("test_doub") == 1.23)
    }


    @Test fun test_list() {
        val conf = Config.of(app, Confs.CONFIG_DEFAULT_PROPERTIES)
        val items = conf.getList("test_ints", Int::class.java)

        Assert.assertTrue(items[0] == 1)
        Assert.assertTrue(items[1] == 22)
        Assert.assertTrue(items[2] == 33)
        Assert.assertTrue(items[3] == 44)
    }


    @Test fun test_map() {
        val conf = Config.of(app, Confs.CONFIG_DEFAULT_PROPERTIES)
        val items = conf.getMap("test_maps", String::class.java, Int::class.java)

        Assert.assertTrue(items["a"] == 1)
        Assert.assertTrue(items["b"] == 22)
        Assert.assertTrue(items["c"] == 33)
        Assert.assertTrue(items["d"] == 44)
    }


    @Test fun test_model_env() {
        val conf  = Config.of(app, Confs.CONFIG_DEFAULT_PROPERTIES)
        val env = conf.env()
        Assert.assertTrue(env.name == "local")
        Assert.assertTrue(env.mode == EnvMode.Dev)
    }


    @Test fun test_model_db_con() {
        val conf  = Config.of(app, Confs.CONFIG_DEFAULT_PROPERTIES)
        val con = conf.dbCon("db1")
        Assert.assertTrue(con.driver == "mysql")
        Assert.assertTrue(con.url == "localhost")
        Assert.assertTrue(con.user == "root")
        Assert.assertTrue(con.pswd == "12345678")
    }


//    @Test fun test_model_db_con2() {
//        val conf  = Config(Confs.CONFIG_DEFAULT_PROPERTIES)
//        val con = conf.dbCon("db")
//        Assert.assertTrue(con.driver == "mysql")
//        Assert.assertTrue(con.url == "localhost")
//        Assert.assertTrue(con.user == "root")
//        Assert.assertTrue(con.password == "12345678")
//    }


    @Test fun test_model_movie() {
        val conf  = Config.of(app, Confs.CONFIG_DEFAULT_PROPERTIES)
        val movie = conf.map<Movie>("movie", Movie::class, null)!!
        Assert.assertTrue(movie.id == 0L )
        Assert.assertTrue(movie.title == "Indiana Jones")
        Assert.assertTrue(movie.category == "adventure")
        Assert.assertTrue(!movie.playing )
        Assert.assertTrue(movie.cost == 30)
        Assert.assertTrue(movie.rating == 4.8)
        Assert.assertTrue(movie.released == DateTimes.of(1981, 6, 12))
    }


    @Test fun test_model_creds() {
        val conf  = Config.of(app, Confs.CONFIG_DEFAULT_PROPERTIES)
        val login = conf.login("login")
        Assert.assertTrue(login.id     == "user1")
        Assert.assertTrue(login.name   == "user one")
        Assert.assertTrue(login.email  == "user1@abc.com")
        Assert.assertTrue(login.region == "us")
        Assert.assertTrue(login.key    == "abcd")
        Assert.assertTrue(login.env    == "dev")
    }


    @Test fun test_model_api_key() {
        val conf  = Config.of(Confs.CONFIG_DEFAULT_PROPERTIES)
        val key = conf.apiLogin("aws-sqs")
        matchkey(key, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_read_api_from() {
        val key = Confs.readApiKey("usr://.slatekit/conf/env.conf", sectionName = "aws-sqs")
        matchkey(key!!, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_loading_from_dir_user() {
        val conf  = Config.of(app, "usr://.slatekit/conf/env.conf")
        val key = conf.apiLogin("aws-sqs")
        matchkey(key, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_loading_from_dir_explicit() {
        val conf  = Config.of(app, "abs:///Users/kishorereddy/.slatekit/conf/env.conf")
        val key = conf.apiLogin("aws-sqs")
        matchkey(key, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_enc() {
        val conf  = Config.of(Confs.CONFIG_DEFAULT_PROPERTIES, MyEncryptor)
        val raw = "StarTrek2100"
        var enc = MyEncryptor.encrypt(raw)

        Assert.assertTrue(raw == conf.getString("enc.raw"))
        Assert.assertTrue(raw == conf.getString("enc.enc"))
    }


    @Test fun test_inheritance() {
        val conf = Confs.loadWithFallbackConfig("jar://env.dev.conf", "jar://env.conf", null)
        Assert.assertTrue(conf.getString("env.name") == "dev")
        Assert.assertTrue(conf.getString("root_name") == "parent env config")
    }


    @Test fun test_map_settings() {
        val bValue = true
        val sValue = 0.toShort()
        val iValue = 1
        val lValue = 2.toLong()
        val dValue = 3.toDouble()

        val localDate = LocalDate.of(2020, 3, 1)
        val localTime = LocalTime.of(9, 30, 45)
        val localDateTime = LocalDateTime.of(2020, 3, 1, 9, 30, 45)
        val zonedDateTime = localDateTime.zoned()
        val uuid = UUID.randomUUID()
        val uniqueId = UPIDs.create()

        val settings = MapSettings()
        settings.putString("sString", "abc")
        settings.putBool("sBool", bValue)
        settings.putShort("sShort", sValue)
        settings.putInt("sInt", iValue)
        settings.putLong("sLong", lValue)
        settings.putDouble("sDouble", dValue)
        settings.putLocalDate("sLocalDate", localDate)
        settings.putLocalTime("sLocalTime", localTime)
        settings.putLocalDateTime("sLocalDateTime", localDateTime)
        settings.putZonedDateTime("sZonedDateTime", zonedDateTime)
        settings.putUUID("sUUID", uuid)
        settings.putUPID("sUniqueId", uniqueId)

        Assert.assertEquals(bValue, settings.getBool("sBool"))
        Assert.assertEquals(sValue, settings.getShort("sShort"))
        Assert.assertEquals(iValue, settings.getInt("sInt"))
        Assert.assertEquals(lValue, settings.getLong("sLong"))
        Assert.assertEquals(dValue.toString(), settings.getDouble("sDouble").toString())
        Assert.assertEquals(localDate, settings.getLocalDate("sLocalDate"))
        Assert.assertEquals(localTime, settings.getLocalTime("sLocalTime"))
        Assert.assertEquals(localDateTime, settings.getLocalDateTime("sLocalDateTime"))
    }


    fun matchkey(actual: ApiLogin, expected: ApiLogin):Unit {
        Assert.assertTrue(expected.account == actual.account)
        Assert.assertTrue(expected.key == actual.key)
        Assert.assertTrue(expected.env == actual.env)
        Assert.assertTrue(expected.pass == actual.pass)
        Assert.assertTrue(expected.tag == actual.tag)
    }
}
