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
import slatekit.apis.core.Api
import slatekit.apis.Setup
import slatekit.apis.Verbs
import slatekit.common.info.Credentials
import slatekit.common.CommonRequest
import slatekit.common.requests.Source
import slatekit.common.toResponse
import slatekit.results.Codes
import slatekit.results.Err
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.builders.Notices
import test.apis.samples.Sample_API_1_Core
import test.apis.samples.Sample_API_1_Protocol
import test.setup.UserApi

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Protocol_Tests : ApiTestsBase() {

    val AREA = "samples"
    val NAME = "core"


    @Test
    fun should_work_when_setup_as_protocol_CLI_request_is_CLI_via_parent() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processParent.name}", Verbs.Read, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via parent:abc").toResponse()
        )
    }


    @Test
    fun should_work_when_setup_as_protocol_CLI_request_is_CLI_explicit() {
        ensure(
                protocol = Protocol.CLI,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processCLI.name}", Verbs.Read, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via cli:abc").toResponse()
        )
    }


    @Test
    fun should_work_when_setup_as_protocol_all_request_is_CLI_via_parent() {
        ensure(
                protocol = Protocol.All,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processParent.name}", Verbs.Read, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via parent:abc").toResponse()
        )
    }


    @Test
    fun should_work_when_setup_as_protocol_all_request_is_CLI_explicit() {
        ensure(
                protocol = Protocol.All,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processCLI.name}", Verbs.Read, mapOf(), mapOf(Pair("name", "abc"))),
                response = Success("ok", msg = "via cli:abc").toResponse()
        )
    }


    @Test fun should_work_when_setup_as_parent_protocol_CLI_and_request_is_CLI() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = null,
                request  = CommonRequest.cli("app.users.protocolParent",  listOf(), listOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("protocolParent", msg="1 abc").toResponse()
        )
    }


    @Test fun should_FAIL_when_setup_as_protocol_WEB_and_request_is_CLI() {
        ensure(
                protocol = Protocol.Web,
                apis = listOf(Api(Sample_API_1_Protocol(), setup = Setup.Annotated)),
                user = null,
                request = CommonRequest.path("$AREA.$NAME.${Sample_API_1_Protocol::processWeb.name}", Verbs.Post, mapOf(), mapOf(Pair("name", "abc"))),
                response = Failure(Err.of("ErrorInfo(msg=api route samples core Sample_API_1_Protocol::processCLI.name not found, err=null, ref=null)"), msg = "Errored").toResponse(),
                checkFailMsg = true
        )
    }
}
