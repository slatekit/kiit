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
import slatekit.apis.containers.ApiContainerCLI
import slatekit.apis.core.Auth
import slatekit.apis.core.Errors
import slatekit.apis.support.ApiHelper
import slatekit.common.ApiKey
import slatekit.common.Result
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.common.results.BAD_REQUEST
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.SUCCESS
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.integration.AppApi
import slatekit.integration.VersionApi
import slatekit.test.common.MyAuthProvider
import slatekit.tests.common.UserApi
import test.common.MyEncryptor
import test.common.User

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Protocol_Tests : ApiTestsBase() {

    // ===================================================================
    //describe( "API Container Type CLI" ) {
    @Test fun should_work_when_setup_as_protocol_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolAny",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolAny", msg="1 abc")
        )
    }


    @Test fun should_work_when_setup_as_protocol_CLI_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "cli", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolCLI",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolCLI", msg="1 abc")
        )
    }


    @Test fun should_work_when_setup_as_parent_protocol_CLI_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolParent",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("protocolParent", msg="1 abc")
        )
    }


    @Test fun should_FAIL_when_setup_as_protocol_WEB_and_request_is_CLI() {
        ensureCall(listOf(ApiReg(UserApi(ctx))),
                "cli", "web", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"), null,
                "app.users.protocolWeb",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                notFound<String>(msg = "app.users.protocolWeb not found")
        )
    }
}