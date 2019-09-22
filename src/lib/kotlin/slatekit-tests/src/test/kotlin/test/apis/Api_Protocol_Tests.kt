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
import slatekit.apis.security.AllProtocols
import slatekit.apis.security.CliProtocol
import slatekit.common.info.Credentials
import slatekit.common.CommonRequest
import slatekit.common.requests.Source
import slatekit.common.toResponse
import slatekit.results.Codes
import slatekit.results.Success
import slatekit.results.builders.Notices
import test.setup.UserApi

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Protocol_Tests : ApiTestsBase() {

    // ===================================================================
    //describe( "API Container Type CLI" ) {
    @Test fun should_work_when_setup_as_protocol_all_request_is_CLI() {
        ensure(
                protocol = CliProtocol,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = CommonRequest.path("app.users.protocolAny", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("protocolAny", msg="1 abc").toResponse()
        )
    }


    @Test fun should_work_when_setup_as_protocol_CLI_and_request_is_CLI() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = CommonRequest.cli("app.users.protocolCLI",  listOf(), listOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("protocolCLI", msg="1 abc").toResponse()
        )
    }


    @Test fun should_work_when_setup_as_parent_protocol_CLI_and_request_is_CLI() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = CommonRequest.cli("app.users.protocolParent",  listOf(), listOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("protocolParent", msg="1 abc").toResponse()
        )
    }


    @Test fun should_FAIL_when_setup_as_protocol_WEB_and_request_is_CLI() {
        ensure(
                protocol = CliProtocol,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = (CommonRequest.cli("app.users.protocolWeb",  listOf(), listOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )) as CommonRequest).copy(source = Source.Web),
                response = Notices.errored<String>("app.users.protocolWeb not found", Codes.NOT_FOUND).toResponse()
        )
    }
}
