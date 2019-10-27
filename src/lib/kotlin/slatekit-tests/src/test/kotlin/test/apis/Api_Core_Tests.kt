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
import slatekit.apis.Protocol
import slatekit.apis.Verbs
import slatekit.apis.core.Api
import slatekit.apis.setup.Setup
import slatekit.common.auth.Roles
import slatekit.common.info.Credentials
import slatekit.common.CommonRequest
import slatekit.common.toResponse
import slatekit.results.Failure
import slatekit.results.Success
import test.apis.samples.Sample_API_1_Core
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Core_Tests : ApiTestsBase() {

    val AREA = "samples"
    val NAME = "core"


    @Test fun can_execute_public_action() {

        ensure(
            protocol = Protocol.CLI,
            apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
            user     = null,
            request  = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processEmpty.name}", Verbs.Read, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
            )),
            response = Success("ok", msg = "no inputs").toResponse()
        )
    }


    @Test fun can_execute_with_type_raw_request() {
        ensure(
            protocol = Protocol.CLI,
            apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("$AREA.$NAME.argTypeRequest", Verbs.Read, mapOf(), mapOf(
                Pair("id", "2")
            )),
            response = Success("ok", msg = "raw send id: 2").toResponse()
        )
    }


    @Test fun can_execute_with_type_raw_meta() {
        ensure(
                protocol = Protocol.CLI,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("$AREA.$NAME.argTypeMeta", Verbs.Read, mapOf(
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
                protocol = Protocol.CLI,
                apis     = listOf(Api(SampleErrorsApi(), "app", "sampleErrors", roles = listOf(Roles.none), declaredOnly = false)),
                user     = null,
                request  = CommonRequest.path("app.sampleErrors.parseNumberWithResults", Verbs.Read, mapOf(), mapOf(
                        "text" to number
                )),
                response = Failure( "$number is not a valid number").toResponse()
        )
    }


    @Test fun can_get_list() {
        ensure(
            protocol = Protocol.CLI,
            apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("$AREA.$NAME.argTypeListInt", Verbs.Read, mapOf(), mapOf(
                Pair("items", listOf(1,2,3) )
            )),
            response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test fun can_get_list_via_conversion() {
        ensure(
            protocol = Protocol.CLI,
            apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("$AREA.$NAME.argTypeListInt", Verbs.Read, mapOf(), mapOf(
                Pair("items", "1,2,3")
            )),
            response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test fun can_get_map() {
        ensure(
            protocol = Protocol.CLI,
            apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("$AREA.$NAME.argTypeMapInt", Verbs.Read, mapOf(), mapOf(
                Pair("items", mapOf("a" to 1, "b" to 2))
            )),
            response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }


    @Test fun can_get_map_via_conversion() {
        ensure(
            protocol = Protocol.CLI,
            apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
            user     = Credentials(name = "kishore", roles = "dev"),
            request  = CommonRequest.path("$AREA.$NAME.argTypeMapInt", Verbs.Read, mapOf(), mapOf(
                Pair("items", "a=1,b=2")
            )),
            response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }

}
