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
import slatekit.apis.Protocol
import slatekit.apis.Verbs
import slatekit.apis.core.Api
import slatekit.apis.Setup
import slatekit.common.CommonRequest
import slatekit.common.DateTimes
import slatekit.common.ext.toResponse
import slatekit.results.Failure
import slatekit.results.Success
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
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processEmpty.name}", Verbs.Get, mapOf(), mapOf(Pair("code", "1"), Pair("tag", "abc"))),
                response = Success("ok", msg = "no inputs").toResponse()
        )
    }


    @Test
    fun can_prevent_private_method_execution() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.privateMethod", Verbs.Get, mapOf(), mapOf(Pair("code", "1"), Pair("tag", "abc"))),
                response = Failure("api route samples core privateMethod not found", msg = "Errored").toResponse(),
                checkFailMsg = true
        )
    }


    @Test
    fun can_execute_with_explicit_name() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.checkName", Verbs.Get, mapOf(), mapOf(Pair("name", "hi 123"))),
                response = Success("ok", msg = "hi 123 ok").toResponse()
        )
    }


    @Test
    fun can_execute_with_inputs() {
        val zone = ZoneId.of("EST")
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputs.name}", Verbs.Get, mapOf(), mapOf(
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
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processRequest.name}", Verbs.Get, mapOf(), mapOf(Pair("id", "2"))),
                response = Success("ok", msg = "raw send id: 2").toResponse()
        )
    }


    @Test
    fun can_execute_with_type_raw_meta() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processMeta.name}", Verbs.Get, mapOf(Pair("token", "abc")), mapOf(Pair("id", "2"))),
                response = Success("ok", msg = "raw meta token: abc").toResponse()
        )
    }


    @Test
    fun can_run_functional_error() {
        val number = "abc"
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processError.name}", Verbs.Get, mapOf(), mapOf("text" to number)),
                response = Failure("$number is not a valid number").toResponse()
        )
    }


    @Test
    fun can_get_list() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputListInt.name}", Verbs.Get, mapOf(), mapOf(Pair("items", listOf(1, 2, 3)))),
                response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test
    fun can_get_list_via_conversion() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputListString.name}", Verbs.Get, mapOf(), mapOf(Pair("items", "1,2,3"))),
                response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test
    fun can_get_map() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputMap.name}", Verbs.Get, mapOf(), mapOf(Pair("items", mapOf("a" to 1, "b" to 2)))),
                response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }


    @Test
    fun can_get_map_via_conversion() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Core(ctx), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputMap.name}", Verbs.Get, mapOf(), mapOf(Pair("items", "a=1,b=2"))),
                response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }
}
