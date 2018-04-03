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
package test.apis

import org.junit.Test
import slatekit.apis.*
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.apis.core.Auth
import slatekit.apis.helpers.ApiHelper
import slatekit.common.*
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.success
import slatekit.entities.core.Entities
import slatekit.integration.common.AppEntContext
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Core_Tests : ApiTestsBase() {


    @Test fun can_execute_public_action() {

        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = null,
            request  = Request.path("app.users.rolesNone", "get", mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
            )),
            response = success("rolesNone", msg = "1 abc").toResponse()
        )
    }


    @Test fun can_execute_with_type_raw_request() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = Request.path("app.users.argTypeRequest", "get", mapOf(), mapOf(
                Pair("id", "2")
            )),
            response = success("ok", msg = "raw request id: 2").toResponse()
        )
    }


    @Test fun can_run_functional_error() {
        val number = "abc"
        ensure(
                protocol = CliProtocol,
                apis     = listOf(Api(SampleErrorsApi(), "app", "sampleErrors", declaredOnly = false)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.sampleErrors.parseNumberWithResults", "get", mapOf(), mapOf(
                        "text" to number
                )),
                response = ResultFuncs.failure<Any>("$number is not a valid number").toResponse()
        )
    }


    @Test fun can_get_list() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = Request.path("app.users.argTypeListInt", "get", mapOf(), mapOf(
                Pair("items", listOf(1,2,3) )
            )),
            response = success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test fun can_get_list_via_conversion() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = Request.path("app.users.argTypeListInt", "get", mapOf(), mapOf(
                Pair("items", "1,2,3")
            )),
            response = success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test fun can_get_map() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = Request.path("app.users.argTypeMapInt", "get", mapOf(), mapOf(
                Pair("items", mapOf("a" to 1, "b" to 2))
            )),
            response = success("ok", msg = ",a=1,b=2").toResponse()
        )
    }


    @Test fun can_get_map_via_conversion() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = Request.path("app.users.argTypeMapInt", "get", mapOf(), mapOf(
                Pair("items", "a=1,b=2")
            )),
            response = success("ok", msg = ",a=1,b=2").toResponse()
        )
    }

}
