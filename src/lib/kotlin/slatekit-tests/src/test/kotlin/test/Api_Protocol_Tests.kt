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
package test

import org.junit.Test
import slatekit.apis.*
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Protocol_Tests : ApiTestsBase() {

    // ===================================================================
    //describe( "API Container Type CLI" ) {
    @Test fun should_work_when_setup_as_protocol_and_request_is_CLI() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolAny",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolAny", msg="1 abc")
        )
    }


    @Test fun should_work_when_setup_as_protocol_CLI_and_request_is_CLI() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "cli", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolCLI",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolCLI", msg="1 abc")
        )
    }


    @Test fun should_work_when_setup_as_parent_protocol_CLI_and_request_is_CLI() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolParent",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolParent", msg="1 abc")
        )
    }


    @Test fun should_FAIL_when_setup_as_protocol_WEB_and_request_is_CLI() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "cli", "web", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolWeb",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                notFound<String>(msg = "app.users.protocolWeb not found")
        )
    }
}