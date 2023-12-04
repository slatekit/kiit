/**
 <kiit_header>
url: www.kiit.dev
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package test.apis

import test.apis.samples.Sample_API_1_Core
import org.junit.Test
import kiit.apis.*
import kiit.apis.setup.GlobalVersion
import kiit.apis.setup.api
import kiit.apis.setup.routes
import kiit.common.Source
import kiit.common.*
import kiit.context.AppContext
import kiit.requests.CommonRequest
import kiit.requests.toResponse
import kiit.results.Failure
import kiit.results.Success
import org.threeten.bp.ZoneId
import test.setup.SampleApiWithConfigSetup

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_002_Executor_Tests : ApiTestsBase() {

    val AREA = "samples"
    val NAME = "core"
    val context = AppContext.simple(Sample_API_1_Core::class.java, "test")


    @Test
    fun can_execute_public_action() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processEmpty.name}", Verbs.POST, mapOf(), mapOf(Pair("code", "1"), Pair("tag", "abc"))),
            response = Success("ok", msg = "no inputs").toResponse()
        )
    }


    @Test
    fun can_execute_with_annotated_action_name() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.checkName", Verbs.POST, mapOf(), mapOf(Pair("name", "hi 123"))),
            response = Success("ok", msg = "hi 123 ok").toResponse()
        )
    }


    @Test
    fun can_execute_with_inputs() {
        val zone = ZoneId.of("EST")
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputs.name}", Verbs.POST, mapOf(), mapOf(
                    Pair("phone", "p1"),
                    Pair("code" , 2   ),
                    Pair("isOn" , true),
                    Pair("date" , DateTimes.of(2019, 10, 30, 8, 30, 45, 0, zone).toString())
            )),
            response = Success("ok", msg = "inputs p1, 2, true, 2019-10-30T08:30:45-05:00[EST]").toResponse()
        )
    }

    @Test
    fun can_execute_with_redirect() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(SampleApiWithConfigSetup::class, SampleApiWithConfigSetup(), setup = SetupType.Config, content = Api_001_Loader_Tests.JSON_REDIRECTS))))),
            user = null,
            request = CommonRequest.path("tests.redirects.adder", Verbs.POST, mapOf(), mapOf(
                Pair("a" , 1   ),
                Pair("b" , 2   )
            )),
            response = Success(3).toResponse()
        )
    }


    @Test
    fun can_execute_with_type_raw_request() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processRequest.name}", Verbs.POST, mapOf(), mapOf(Pair("id", "2"))),
            response = Success("ok", msg = "raw send id: 2").toResponse()
        )
    }


    @Test
    fun can_execute_with_type_raw_meta() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processMeta.name}", Verbs.POST, mapOf(Pair("token", "abc")), mapOf(Pair("id", "2"))),
            response = Success("ok", msg = "raw meta token: abc").toResponse()
        )
    }


    @Test
    fun can_run_functional_error() {
        val number = "abc"
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processError.name}", Verbs.POST, mapOf(), mapOf("text" to number)),
            response = Failure("$number is not a valid number").toResponse()
        )
    }


    @Test
    fun can_get_list() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputListInt.name}", Verbs.POST, mapOf(), mapOf(Pair("items", listOf(1, 2, 3)))),
            response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test
    fun can_get_list_via_conversion() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputListString.name}", Verbs.POST, mapOf(), mapOf(Pair("items", "1,2,3"))),
            response = Success("ok", msg = ",1,2,3").toResponse()
        )
    }


    @Test
    fun can_get_map() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputMap.name}", Verbs.POST, mapOf(), mapOf(Pair("items", mapOf("a" to 1, "b" to 2)))),
            response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }


    @Test
    fun can_get_map_via_conversion() {
        checkCall(
            protocol = Source.CLI,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Core::class, Sample_API_1_Core(context)))))),
            user = null,
            request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Core::processInputMap.name}", Verbs.POST, mapOf(), mapOf(Pair("items", "a=1,b=2"))),
            response = Success("ok", msg = ",a=1,b=2").toResponse()
        )
    }
}
