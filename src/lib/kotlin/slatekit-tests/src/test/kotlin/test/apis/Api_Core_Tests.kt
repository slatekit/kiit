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
import org.threeten.bp.ZoneId
import slatekit.apis.Verbs
import slatekit.apis.routes.Api
import slatekit.apis.SetupType
import kiit.requests.CommonRequest
import kiit.common.DateTimes
import kiit.common.Source
import kiit.requests.toResponse
import kiit.results.Codes
import kiit.results.Failure
import kiit.results.Success
import test.apis.samples.Sample_API_1_Core

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Core_Tests : ApiTestsBase() {

    val AREA = "samples"
    val NAME = "core"


    @Test
    fun can_execute_public_action() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processEmpty.name}", Verbs.GET, mapOf(), mapOf(Pair("code", "1"), Pair("tag", "abc"))),
                response = Success("ok", msg = "no inputs").toResponse()
        )
    }


    @Test
    fun can_prevent_private_method_execution() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.privateMethod", Verbs.GET, mapOf(), mapOf(Pair("code", "1"), Pair("tag", "abc"))),
                response = Failure("api route samples core privateMethod not found", Codes.NOT_FOUND).toResponse(),
                checkFailMsg = true
        )
    }


    @Test
    fun can_execute_with_explicit_name() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.checkName", Verbs.GET, mapOf(), mapOf(Pair("name", "hi 123"))),
                response = Success("ok", msg = "hi 123 ok").toResponse()
        )
    }


    @Test
    fun can_execute_with_inputs() {
        val zone = ZoneId.of("EST")
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputs.name}", Verbs.GET, mapOf(), mapOf(
                        Pair("phone", "p1"),
                        Pair("code" , 2   ),
                        Pair("isOn" , true),
                        Pair("date" , DateTimes.of(2019, 10, 30, 8, 30, 45, 0, zone).toString())
                )),
                response = Success("ok", msg = "inputs p1, 2, true, 2019-10-30T08:30:45-05:00[EST]").toResponse()
        )
    }


    @Test
    fun can_execute_with_type_raw_request() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processRequest.name}", Verbs.GET, mapOf(), mapOf(Pair("id", "2"))),
                response = Success("ok", msg = "raw send id: 2").toResponse()
        )
    }


    @Test
    fun can_execute_with_type_raw_meta() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processMeta.name}", Verbs.GET, mapOf(Pair("token", "abc")), mapOf(Pair("id", "2"))),
                response = Success("ok", msg = "raw meta token: abc").toResponse()
        )
    }


    @Test
    fun can_run_functional_error() {
        val number = "abc"
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processError.name}", Verbs.GET, mapOf(), mapOf("text" to number)),
                response = Failure("$number is not a valid number").toResponse()
        )
    }


    @Test
    fun can_get_list() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputListInt.name}", Verbs.GET, mapOf(), mapOf(Pair("items", listOf(1, 2, 3)))),
                response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test
    fun can_get_list_via_conversion() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputListString.name}", Verbs.GET, mapOf(), mapOf(Pair("items", "1,2,3"))),
                response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test
    fun can_get_map() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputMap.name}", Verbs.GET, mapOf(), mapOf(Pair("items", mapOf("a" to 1, "b" to 2)))),
                response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }


    @Test
    fun can_get_map_via_conversion() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = SetupType.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputMap.name}", Verbs.GET, mapOf(), mapOf(Pair("items", "a=1,b=2"))),
                response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }
}
