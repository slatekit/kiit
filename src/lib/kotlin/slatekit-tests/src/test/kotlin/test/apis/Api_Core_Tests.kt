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
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.apis.security.CliProtocol
import slatekit.common.auth.Roles
import slatekit.common.info.Credentials
import slatekit.common.CommonRequest
import slatekit.common.toResponse
import slatekit.results.Failure
import slatekit.results.Success
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
            request  = CommonRequest.path("app.users.rolesNone", "get", mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
            )),
            response = Success("rolesNone", msg = "1 abc").toResponse()
        )
    }


    @Test fun can_execute_with_type_raw_request() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("app.users.argTypeRequest", "get", mapOf(), mapOf(
                Pair("id", "2")
            )),
            response = Success("ok", msg = "raw send id: 2").toResponse()
        )
    }


    @Test fun can_execute_with_type_raw_meta() {
        ensure(
                protocol = CliProtocol,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.users.argTypeMeta", "get", mapOf(
                        Pair("token", "abc")
                ), mapOf(
                        Pair("id", "2")
                )),
                response = Success("ok", msg = "raw meta token: abc").toResponse()
        )
    }


    @Test fun can_run_functional_error() {
        val number = "abc"
        ensure(
                protocol = CliProtocol,
                apis     = listOf(Api(SampleErrorsApi(), "app", "sampleErrors", roles = Roles.none, declaredOnly = false)),
                user     = null,
                request  = CommonRequest.path("app.sampleErrors.parseNumberWithResults", "get", mapOf(), mapOf(
                        "text" to number
                )),
                response = Failure( "$number is not a valid number").toResponse()
        )
    }


    @Test fun can_get_list() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("app.users.argTypeListInt", "get", mapOf(), mapOf(
                Pair("items", listOf(1,2,3) )
            )),
            response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test fun can_get_list_via_conversion() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("app.users.argTypeListInt", "get", mapOf(), mapOf(
                Pair("items", "1,2,3")
            )),
            response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test fun can_get_map() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("app.users.argTypeMapInt", "get", mapOf(), mapOf(
                Pair("items", mapOf("a" to 1, "b" to 2))
            )),
            response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }


    @Test fun can_get_map_via_conversion() {
        ensure(
            protocol = CliProtocol,
            apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("app.users.argTypeMapInt", "get", mapOf(), mapOf(
                Pair("items", "a=1,b=2")
            )),
            response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }

}
