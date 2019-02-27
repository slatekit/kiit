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

import org.junit.Test
import slatekit.common.info.ApiLogin
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.conf.ConfFuncs
import slatekit.common.conf.Config
import slatekit.common.envs.EnvMode
import slatekit.meta.map
import test.setup.Movie
import test.setup.MyEncryptor
import java.util.*
import java.io.FileInputStream



/**
 * Created by kishorereddy on 6/4/17.
 */
class ConfigTests {
    fun load(): Properties {
        val file = this.javaClass.getResource("/" + ConfFuncs.CONFIG_DEFAULT_PROPERTIES).file
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
        val conf = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        assert(conf.getInt("test_int") == 1)
        assert(conf.getBool("test_bool"))
        assert(conf.getString("test_text") == "abc")
        assert(conf.getLong("test_long") == 10L)
        assert(conf.getDouble("test_doub") == 1.23)
    }


    @Test fun test_list() {
        val conf = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        val items = conf.getList("test_ints", Int::class.java)

        assert(items[0] == 1)
        assert(items[1] == 22)
        assert(items[2] == 33)
        assert(items[3] == 44)
    }


    @Test fun test_map() {
        val conf = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        val items = conf.getMap("test_maps", String::class.java, Int::class.java)

        assert(items["a"] == 1)
        assert(items["b"] == 22)
        assert(items["c"] == 33)
        assert(items["d"] == 44)
    }


    @Test fun test_model_env() {
        val conf  = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        val env = conf.env()
        assert(env.name == "local")
        assert(env.mode == EnvMode.Dev)
    }


    @Test fun test_model_db_con() {
        val conf  = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        val con = conf.dbCon("db1")
        assert(con.driver == "mysql")
        assert(con.url == "localhost")
        assert(con.user == "root")
        assert(con.password == "12345678")
    }


    @Test fun test_model_movie() {
        val conf  = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        val movie = conf.map<Movie>("movie", Movie::class, null)!!
        assert(movie.id == 0L )
        assert(movie.title == "Indiana Jones")
        assert(movie.category == "adventure")
        assert(!movie.playing )
        assert(movie.cost == 30)
        assert(movie.rating == 4.8)
        assert(movie.released == DateTimes.of(1981, 6, 12))
    }


    @Test fun test_model_creds() {
        val conf  = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        val login = conf.login("login")
        assert(login.id     == "user1")
        assert(login.name   == "user one")
        assert(login.email  == "user1@abc.com")
        assert(login.region == "us")
        assert(login.key    == "abcd")
        assert(login.env    == "dev")
    }


    @Test fun test_model_api_key() {
        val conf  = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
        val key = conf.apiLogin("aws-sqs")
        matchkey(key, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_read_api_from() {
        val key = ConfFuncs.readApiKey("user://.slatekit/conf/env.conf", sectionName = "aws-sqs")
        matchkey(key!!, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_loading_from_dir_user() {
        val conf  = Config("user://.slatekit/conf/env.conf")
        val key = conf.apiLogin("aws-sqs")
        matchkey(key, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_loading_from_dir_explicit() {
        val conf  = Config("file:///Users/kishore.reddy/.slatekit/conf/env.conf")
        val key = conf.apiLogin("aws-sqs")
        matchkey(key, ApiLogin("mycompany1.dev", "key1", "pass1", "env1", "tag1"))
    }


    @Test fun test_enc() {
        val conf  = Config(ConfFuncs.CONFIG_DEFAULT_PROPERTIES, MyEncryptor)
        val raw = "StarTrek2100"
        var enc = MyEncryptor.encrypt(raw)

        assert(raw == conf.getString("enc.raw"))
        assert(raw == conf.getString("enc.enc"))
    }


    @Test fun test_inheritance() {
        val conf = ConfFuncs.loadWithFallbackConfig("jars://env.dev.conf", "jars://env.conf", null)
        assert(conf.getString("env.name") == "dev")
        assert(conf.getString("root_name") == "parent env config")
    }


    fun matchkey(actual: ApiLogin, expected: ApiLogin):Unit {
        assert(expected.account == actual.account)
        assert(expected.key == actual.key)
        assert(expected.env == actual.env)
        assert(expected.pass == actual.pass)
        assert(expected.tag == actual.tag)
    }
}
