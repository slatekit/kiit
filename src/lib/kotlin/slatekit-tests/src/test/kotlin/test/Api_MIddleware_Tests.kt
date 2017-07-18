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


class Api_MIddleware_Tests : ApiTestsBase() {


    // ===================================================================
    //describe( "API Container with middleware" ) {
    @Test fun middleware_with_hooks() {
        val userApi = UserApi(ctx, true)
        val keys = buildKeys()
        val auth = MyAuthProvider("kishore", "dev,ops,admin", keys)

        val apis = ApiContainerCLI(ctx,
                apis = listOf(ApiReg(userApi)),
                auth = auth
        )

        val r1 = apis.call("app", "users", "rolesSpecific", "get",
                mapOf("api-key" to "3E35584A8DE0460BB28D6E0D32FB4CFD"),
                mapOf("code" to 123, "tag" to "abc"))

        val r2 = apis.call("app", "users", "rolesParent", "get",
                mapOf("api-key" to "54B1817194C1450B886404C6BEA81673"),
                mapOf("code" to 123, "tag" to "abc"))

        assert(userApi.onBeforeHookCount.size == 2)
        assert(userApi.onAfterHookCount.size == 2)
        assert(userApi.onBeforeHookCount[0].path == "app.users.rolesSpecific")
        assert(userApi.onBeforeHookCount[1].path == "app.users.rolesParent")
        assert(userApi.onAfterHookCount[0].path == "app.users.rolesSpecific")
        assert(userApi.onAfterHookCount[1].path == "app.users.rolesParent")
    }


    @Test fun middleware_with_filters_request_filtered_out() {
        val userApi = UserApi(ctx, false, true)
        val keys = buildKeys()
        val auth = MyAuthProvider("kishore", "dev,ops,admin", keys)

        val apis = ApiContainerCLI(ctx,
                apis = listOf(ApiReg(userApi)),
                auth = auth
        )

        val r1 = apis.call("app", "users", "rolesSpecific", "get",
                mapOf("api-key" to "3E35584A8DE0460BB28D6E0D32FB4CFD"),
                mapOf("code" to 123, "tag" to "abc"))

        assert(!r1.success)
        assert(r1.code == BAD_REQUEST)
        assert(r1.msg == "filtered out")
    }


    @Test fun middleware_with_filters_request_ok() {
        val userApi = UserApi(ctx, false, true)
        val keys = buildKeys()
        val auth = MyAuthProvider("kishore", "dev,ops,admin", keys)

        val apis = ApiContainerCLI(ctx,
                apis = listOf(ApiReg(userApi)),
                auth = auth
        )

        val r1 = apis.call("app", "users", "protocolAny", "get",
                mapOf("api-key" to "3E35584A8DE0460BB28D6E0D32FB4CFD"),
                mapOf("code" to 123, "tag" to "abc"))

        assert(r1.success)
        assert(r1.code == SUCCESS)
        assert(r1.value == "protocolAny")
        assert(r1.msg == "123 abc")
    }

}