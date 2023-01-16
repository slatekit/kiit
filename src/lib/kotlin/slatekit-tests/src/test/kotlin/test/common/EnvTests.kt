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

//import org.jetbrains.spek.api.Spek
//import org.jetbrains.spek.api.dsl.*
//import kotlin.test.assertEquals
import org.junit.Assert
import kiit.common.envs.*

import org.junit.Test


class EnvTests {

     fun ensureMatch(envs:Envs, name:String, envType:EnvMode, desc:String) {
        val env = envs.get(name)
        Assert.assertTrue( env != null  )
        Assert.assertTrue( env!!.name == name )
        Assert.assertTrue( env!!.mode.name == envType.name)
        Assert.assertTrue( env!!.desc == desc )
    }


    @Test fun can_construct() {
        val env = Env("qa1", EnvMode.Qat, "nyc")
        Assert.assertTrue( env.name      == "qa1" )
        Assert.assertTrue( env.mode.name == "qat" )
        Assert.assertTrue( env.region    == "nyc" )
    }


    @Test fun default_envs_can_be_created() {
        val envs = Envs.defaults()
        ensureMatch( envs, "loc", EnvMode.Dev , "Dev environment (local)" )
        ensureMatch( envs, "dev", EnvMode.Dev , "Dev environment (shared)" )
        ensureMatch( envs, "qat", EnvMode.Qat  , "QA environment  (current release)" )
        ensureMatch( envs, "stg", EnvMode.Uat , "STG environment (demo)" )
        ensureMatch( envs, "pro", EnvMode.Pro, "LIVE environment" )
    }


    @Test fun default_envs_have_default_current_environment() {
        val envs = Envs.defaults()
        Assert.assertTrue( envs.current != null )
        Assert.assertTrue( envs.name == "loc")
        Assert.assertTrue( envs.env == EnvMode.Dev.name )
        Assert.assertTrue( envs.isDev )
    }


    @Test fun default_envs_can_select_environment() {
        val envAll = Envs.defaults()
        val envs = envAll.select("qat")
        Assert.assertTrue( envs.current != null )
        Assert.assertTrue( envs.name == "qat")
        Assert.assertTrue( envs.env == EnvMode.Qat.name )
        Assert.assertTrue( envs.isQat )
    }


    @Test fun default_envs_can_validate_env_against_defaults() {
        val envAll = Envs.defaults()
        Assert.assertTrue( envAll.isValid("qat") )
        Assert.assertTrue( !envAll.isValid("abc") )
    }


    @Test fun can_build_key() {
        Assert.assertTrue( Env("qa1", EnvMode.Qat).key == "qa1:qat" )
    }


    @Test fun can_check_loc() {
        Assert.assertTrue( Env("loc", EnvMode.Dev).isDev )
    }


    @Test fun can_check_dev() {
        Assert.assertTrue( Env("qa1", EnvMode.Qat).isQat )
    }


    @Test fun can_check_qa() {
        Assert.assertTrue( Env("qa1", EnvMode.Qat).isQat )
    }


    @Test fun can_check_stg() {
        Assert.assertTrue( Env("stg", EnvMode.Uat).isUat )
    }


    @Test fun can_check_pro() {
        Assert.assertTrue( Env("pro", EnvMode.Pro).isPro )
    }


    @Test fun can_parse_with_name() {
        Assert.assertTrue( Env.parse("dev").isDev)
        Assert.assertTrue( Env.parse("qat").isQat)
        Assert.assertTrue( Env.parse("uat").isUat)
        Assert.assertTrue( Env.parse("pro").isPro)
        Assert.assertTrue( Env.parse("dis").isDis)
        Assert.assertTrue( Env.parse("abc").isOther)
    }


    @Test fun can_parse_with_name_and_mode() {
        Assert.assertTrue( Env.parse("qa1:qat").isQat)
    }
}