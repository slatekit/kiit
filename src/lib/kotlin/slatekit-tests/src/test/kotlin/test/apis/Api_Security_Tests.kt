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
import slatekit.common.security.Credentials
import slatekit.common.Request
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.toResponse
import test.setup.UserApi

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Security_TestsTests : ApiTestsBase() {


    // ===================================================================
    //describe( "Authorization: using App roles on actions" ) {
    @Test fun roles_should_work_when_role_is_any() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.users.rolesAny", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = success("rolesAny", msg="1 abc").toResponse()
        )

        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "qa"),
                request  = Request.path("app.users.rolesAny", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = success("rolesAny", msg="1 abc").toResponse()
        )

        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = ""),
                request  = Request.path("app.users.rolesAny", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = unAuthorized<Any>("unauthorized").toResponse()
        )
    }


    @Test fun roles_should_fail_for_any_role_any_with_no_user() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = null,
                request  = Request.path("app.users.rolesAny", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = unAuthorized<String>(msg = "Unable to authorize, authorization provider not set").toResponse()
        )
    }


    @Test fun roles_should_work_for_a_specific_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.users.rolesSpecific", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = success("rolesSpecific", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "ops"),
                request  = Request.path("app.users.rolesSpecific", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = unAuthorized<String>("unauthorized").toResponse()
        )
    }


    @Test fun roles_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = Request.path("app.users.rolesParent", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = success("rolesParent", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.users.rolesParent", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = unAuthorized<String>("unauthorized").toResponse()
        )
    }

    // ===================================================================
    //describe( "Authorization: using Key roles on actions" ) {
    @Test fun roles_by_key_should_work_when_role_is_any() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.users.rolesAny", "get", mapOf(
                        Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = success("rolesAny", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_by_key_should_fail_for_any_role_with_no_user() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = null,
                request  = Request.path("app.users.rolesAny", "get", mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = unAuthorized<String>(msg = "Unable to authorize, authorization provider not set").toResponse()
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.users.rolesSpecific", "get", mapOf(
                        Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = success("rolesSpecific", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "qa"),
                request  = Request.path("app.users.rolesSpecific", "get", mapOf(
                        Pair("api-key", "EB7EB37764AD4411A1763E6A593992BD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = unAuthorized<String>("unauthorized").toResponse()
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = Request.path("app.users.rolesParent", "get", mapOf(
                        Pair("api-key", "54B1817194C1450B886404C6BEA81673")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = success("rolesParent", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensure(
                protocol = AllProtocols,
                apis     = listOf(Api(UserApi(ctx), setup = Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.users.rolesParent", "get", mapOf(
                        Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = unAuthorized<String>("unauthorized").toResponse()
        )
    }
}
