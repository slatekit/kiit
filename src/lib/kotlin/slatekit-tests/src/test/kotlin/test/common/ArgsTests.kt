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


/**
 * Created by kishorereddy on 5/22/17.
 */

import org.junit.Test
import slatekit.common.Result
import slatekit.common.ResultEx
import slatekit.common.ResultMsg
import slatekit.common.args.Args
import slatekit.common.args.ArgsFuncs
import slatekit.common.getOrElse


class ArgsTests {

    @Test fun `can parse args with defaults2`(): Unit {
        println("hello method")
    }


    @Test fun test_m1(){
        `can parse args with defaults2`()
    }


    @Test fun can_parse_args_with_defaults() {

        val result = Args.parse("-env:loc -log:info -region:ny")
        ensure(result, true, 3, listOf(
            Pair("env"   , "loc" ),
            Pair("log"   , "info"),
            Pair("region", "ny"  )
        ))
    }


    @Test fun can_parse_args_with_custom_prefix_and_separator() {

        val result = Args.parse("-env=loc -log=info -region=ny", "-", "=")
        ensure(result, true, 3, listOf(
            Pair("env"   , "loc" ),
            Pair("log"   , "info"),
            Pair("region", "ny"  )
        ))
    }


    @Test fun can_parse_args_single_quotes() {

        val result = Args.parse("-env='loc' -log='info' -region='ny'", "-", "=")
        ensure(result, true, 3, listOf(
            Pair("env"   , "loc" ),
            Pair("log"   , "info"),
            Pair("region", "ny"  )
        ))
    }


    @Test fun can_parse_args_double_quotes() {

        val result = Args.parse("-env=\"loc\" -log=\"info\" -region=\"ny\"", "-", "=")
        ensure(result, true, 3, listOf(
            Pair("env"   , "loc" ),
            Pair("log"   , "info"),
            Pair("region", "ny"  )
        ))
    }


    @Test fun can_parse_args_with_dots() {

        val result = Args.parse("-env.api='loc' -log.level='info' -region.api='ny'", "-", "=")
        ensure(result, true, 3, listOf(
            Pair("env.api"   , "loc" ),
            Pair("log.level"  , "info"),
            Pair("region.api", "ny"  )
        ))
    }


    @Test fun can_parse_arg_values_with_decimals() {

        val result = Args.parse("-env.api='loc' -eg.test=12.34", "-", "=")
        ensure(result, true, 2, listOf(
                Pair("env.api"   , "loc" ),
                Pair("eg.test"   , "12.34" )
        ))
    }


    @Test fun can_parse_arg_values_with_decimals2() {

        val result = Args.parse("-env.api='loc' -eg.test=12.34 -log.level='info'", "-", "=")
        ensure(result, true, 3, listOf(
                Pair("env.api"   , "loc" ),
                Pair("eg.test"   , "12.34" ),
                Pair("log.level"  , "info")
        ))
    }


    @Test fun can_parse_arg_values_with_dots() {

        val result = Args.parse("-num=12.34", "-", "=")
        ensure(result, true, 1, listOf(
                Pair("num"   , "12.34" )
        ))
    }


    @Test fun can_parse_actions_with_args() {

        val result = Args.parse("area.api.action -env.api='loc' -log.level='info' -region.api='ny'", "-", "=", true)
        ensure(result, true, 3, listOf(
            Pair("env.api"   , "loc" ),
            Pair("log.level"  , "info"),
            Pair("region.api", "ny"  )
        ), null, null, listOf("area", "api", "action"))
    }


    @Test fun can_parse_meta_args() {

        val result = Args.parse("area.api.action -env.api='loc' -log.level='info' @api.key='abc123'", "-", "=", true)
        ensure(result, true, 2,

                listOf(
                    Pair("env.api"    , "loc" ),
                    Pair("log.level"  , "info")
                ),
                listOf(
                    Pair("api.key"    , "abc123" )
                ),
                null,
                listOf("area", "api", "action")
        )
    }


    @Test fun can_parse_sys_args() {

        val result = Args.parse("area.api.action -env.api='loc' @api.key='abc123' \$format=json", "-", "=", true)
        ensure(result, true, 1,

                listOf(
                        Pair("env.api"    , "loc" )
                ),
                listOf(
                        Pair("api.key"    , "abc123" )
                ),
                listOf(
                        Pair("format"    , "json" )
                ),
                listOf("area", "api", "action")
        )
    }


    @Test fun can_parse_sys_args_only() {

        // app.reg.createSampleUser $command=sample
        val result = Args.parse("area.api.action \$format=json", "-", "=", true)
        ensure(result, true, 0,
                listOf<Pair<String,String>>(),
                listOf<Pair<String,String>>(),
                listOf(
                        Pair("format"    , "json" )
                ),
                listOf("area", "api", "action")
        )
    }


    // app.users.activate
    @Test fun can_parse_actions_without_args() {

        val result = Args.parse("area.api.action", "-", "=", true)
        ensure(result, true, 0, listOf(), null, null, listOf("area", "api", "action"))
    }


    @Test fun is_meta_arg_with_help() {
        assert( ArgsFuncs.isMetaArg(listOf("help"  ), 0, "help", "info") )
        assert( ArgsFuncs.isMetaArg(listOf("-help" ), 0, "help", "info") )
        assert( ArgsFuncs.isMetaArg(listOf("--help"), 0, "help", "info") )
        assert( ArgsFuncs.isMetaArg(listOf("/help" ), 0, "help", "info") )

        assert( ArgsFuncs.isMetaArg(listOf("info"  ), 0, "help", "info") )
        assert( ArgsFuncs.isMetaArg(listOf("-info" ), 0, "help", "info") )
        assert( ArgsFuncs.isMetaArg(listOf("--info"), 0, "help", "info") )
        assert( ArgsFuncs.isMetaArg(listOf("/info" ), 0, "help", "info") )

        assert( !ArgsFuncs.isMetaArg(listOf("/about" ), 0, "help", "info"))
    }


    @Test fun is_help_on_area() {
        assert( ArgsFuncs.isHelp(listOf("app", "?"), 1) )
    }


    private fun ensure(result: ResultEx<Args>, success:Boolean, size:Int,
                       expectedNamed:List<Pair<String,String>>,
                       expectedMeta:List<Pair<String,String>>? = null,
                       expectedSys:List<Pair<String,String>>? = null,
                       parts:List<String>? = null) : Unit {

        // success / fail
        assert( result.success == success )

        val args = result.getOrElse { Args.default() }

        // size
        assert( args.size() == size)

        // expected
        for(item in expectedNamed){
            assert( args.containsKey(item.first))
            assert( args.getString(item.first) == item.second)
        }

        expectedMeta?.let { metaArgs ->
            for((first, second) in metaArgs){
                assert( args.containsMetaKey(first))
                assert( args.getMetaString(first) == second)
            }
        }

        expectedSys?.let { sysArgs ->
            for((first, second) in sysArgs){
                assert( args.containsSysKey(first))
                assert( args.getSysString(first) == second)
            }
        }

        parts?.let { p ->

            if(p.isNotEmpty()){
                assert(args.actionParts.size == p.size)
                for(i in 0 .. p.size - 1){
                    val part = p[i]
                    assert( args.getVerb(i) == part)
                }
            }
        }
    }

}