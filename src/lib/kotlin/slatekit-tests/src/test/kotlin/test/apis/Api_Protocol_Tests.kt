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
import slatekit.apis.core.Api
import slatekit.apis.Setup
import slatekit.apis.Verbs
import slatekit.common.CommonRequest
import slatekit.common.Source
import slatekit.common.ext.toResponse
import slatekit.results.Err
import slatekit.results.Failure
import slatekit.results.Success
import test.apis.samples.Sample_API_1_Protocol

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Protocol_Tests : ApiTestsBase() {

    val AREA = "samples"
    val NAME = "core"


    @Test
    fun should_work_when_setup_as_protocol_CLI_request_is_CLI_via_parent() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processParent.name}", Verbs.Post, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via parent:abc").toResponse()
        )
    }


    @Test
    fun should_work_when_setup_as_protocol_CLI_request_is_CLI_explicit() {
        ensure(
                protocol = Source.CLI,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processCLI.name}", Verbs.Post, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via cli:abc").toResponse()
        )
    }


    @Test
    fun should_work_when_setup_as_protocol_all_request_is_ALL_via_parent() {
        ensure(
                protocol = Source.All,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processParent.name}", Verbs.Post, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via parent:abc").toResponse()
        )
    }


    @Test fun should_work_when_setup_as_parent_protocol_CLI_and_request_is_CLI() {
        ensure(
                protocol = Source.All,
                apis     = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user     = null,
                request  = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processCLI.name}",  Verbs.Post, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via cli:abc").toResponse()
        )
    }


    @Test
    fun should_fail_when_setup_as_protocol_web_request_is_CLI_explicit() {
        ensure(
                protocol = Source.All,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.web("$AREA", "$NAME", "${Sample_API_1_Protocol::processCLI.name}", Verbs.Post, mapOf(), mapOf(Pair("name", "abc"))),
                response = Failure(Err.of("expected source cli, but got web"), msg = "Errored").toResponse(),
                checkFailMsg = true
        )
    }
}
