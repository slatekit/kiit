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
import slatekit.common.toResponse
import slatekit.results.Success
import slatekit.results.builders.Notices
import test.setup.UserApi

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Security_TestsTests : ApiTestsBase() {


    // ===================================================================
    //describe( "Authorization: using App roles on actions" ) {
    @Test fun roles_should_work_when_role_is_any() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.users.rolesAny", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("rolesAny", msg="1 abc").toResponse()
        )

        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "qa"),
                request  = CommonRequest.path("app.users.rolesAny", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("rolesAny", msg="1 abc").toResponse()
        )

        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = ""),
                request  = CommonRequest.path("app.users.rolesAny", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Notices.denied<Any>("unauthorized").toResponse()
        )
    }


    @Test fun roles_should_fail_for_any_role_any_with_no_user() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = null,
                request  = CommonRequest.path("app.users.rolesAny", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Notices.denied<String>(msg = "Unable to authorize, authorization provider not set").toResponse()
        )
    }


    @Test fun roles_should_work_for_a_specific_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.users.rolesSpecific", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("rolesSpecific", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "ops"),
                request  = CommonRequest.path("app.users.rolesSpecific", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Notices.denied<String>("unauthorized").toResponse()
        )
    }


    @Test fun roles_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = CommonRequest.path("app.users.rolesParent", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("rolesParent", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.users.rolesParent", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Notices.denied<String>("unauthorized").toResponse()
        )
    }

    // ===================================================================
    //describe( "Authorization: using Key roles on actions" ) {
    @Test fun roles_by_key_should_work_when_role_is_any() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.users.rolesAny", Verbs.Get, mapOf(
                        Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("rolesAny", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_by_key_should_fail_for_any_role_with_no_user() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = null,
                request  = CommonRequest.path("app.users.rolesAny", Verbs.Get, mapOf(), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Notices.denied<String>(msg = "Unable to authorize, authorization provider not set").toResponse()
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.users.rolesSpecific", Verbs.Get, mapOf(
                        Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("rolesSpecific", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "qa"),
                request  = CommonRequest.path("app.users.rolesSpecific", Verbs.Get, mapOf(
                        Pair("api-key", "EB7EB37764AD4411A1763E6A593992BD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Notices.denied<String>("unauthorized").toResponse()
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "admin"),
                request  = CommonRequest.path("app.users.rolesParent", Verbs.Get, mapOf(
                        Pair("api-key", "54B1817194C1450B886404C6BEA81673")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Success("rolesParent", msg="1 abc").toResponse()
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensure(
                protocol = Protocol.All,
                apis     = listOf(Api(UserApi(ctx), setup = Setup.Annotated)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.users.rolesParent", Verbs.Get, mapOf(
                        Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                        Pair("code", "1"),
                        Pair("tag", "abc")
                )),
                response = Notices.denied<String>("unauthorized").toResponse()
        )
    }
}
