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
import slatekit.common.envs.*

import org.junit.Test


class EnvTests {

     fun ensureMatch(envs:Envs, name:String, envType:EnvMode, desc:String) {
        val env = envs.get(name)
        assert( env != null  )
        assert( env!!.name == name )
        assert( env!!.mode.name == envType.name)
        assert( env!!.desc == desc )
    }


    @Test fun can_construct() {
        val env = Env("qa1", EnvMode.Qat, "nyc")
        assert( env.name      == "qa1" )
        assert( env.mode.name == "qa"  )
        assert( env.region    == "nyc" )
    }


    @Test fun default_envs_can_be_created() {
        val envs = Env.defaults()
        ensureMatch( envs, "loc", EnvMode.Dev , "Dev environment (local)" )
        ensureMatch( envs, "dev", EnvMode.Dev , "Dev environment (shared)" )
        ensureMatch( envs, "qa1", EnvMode.Qat  , "QA environment  (current release)" )
        ensureMatch( envs, "qa2", EnvMode.Qat  , "QA environment  (last release)" )
        ensureMatch( envs, "stg", EnvMode.Uat , "STG environment (demo)" )
        ensureMatch( envs, "pro", EnvMode.Pro, "LIVE environment" )
    }


    @Test fun default_envs_have_default_current_environment() {
        val envs = Env.defaults()
        assert( envs.current != null )
        assert( envs.name == "loc")
        assert( envs.env == EnvMode.Dev.name )
        assert( envs.isDev )
    }


    @Test fun default_envs_can_select_environment() {
        val envAll = Env.defaults()
        val envs = envAll.select("qa1")
        assert( envs.current != null )
        assert( envs.name == "qa1")
        assert( envs.env == EnvMode.Qat.name )
        assert( envs.isQat )
    }


    @Test fun default_envs_can_validate_env_against_defaults() {
        val envAll = Env.defaults()
        assert( envAll.isValid("qa1") )
        assert( !envAll.isValid("abc") )
    }


    @Test fun can_build_key() {
        assert( Env("qa1", EnvMode.Qat).key == "qa1:qa" )
    }


    @Test fun can_check_loc() {
        assert( Env("loc", EnvMode.Dev).isDev )
    }


    @Test fun can_check_dev() {
        assert( Env("qa1", EnvMode.Qat).isQat )
    }


    @Test fun can_check_qa() {
        assert( Env("qa1", EnvMode.Qat).isQat )
    }


    @Test fun can_check_stg() {
        assert( Env("stg", EnvMode.Uat).isUat )
    }


    @Test fun can_check_pro() {
        assert( Env("pro", EnvMode.Pro).isPro )
    }


    @Test fun can_parse_with_name() {
        assert( Env.parse("dev").isDev)
        assert( Env.parse("qa").isQat)
        assert( Env.parse("uat").isUat)
        assert( Env.parse("pro").isPro)
        assert( Env.parse("dr").isDis)
        assert( Env.parse("abc").isOther)
    }


    @Test fun can_parse_with_name_and_mode() {
        assert( Env.parse("qa1:qa").isQat)
    }
}