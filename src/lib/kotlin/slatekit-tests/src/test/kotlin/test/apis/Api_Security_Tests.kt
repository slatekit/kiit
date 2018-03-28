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
import slatekit.apis.*
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Security_TestsTests : ApiTestsBase() {


    // ===================================================================
    //describe( "Authorization: using App roles on actions" ) {
    @Test fun roles_should_work_when_role_is_any() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"),
                "app.users.rolesAny",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("rolesAny", msg="1 abc")
        )

        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "qa"),
                "app.users.rolesAny",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("rolesAny", msg="1 abc")
        )

        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", ""),
                "app.users.rolesAny",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized("unauthorized")
        )
    }


    @Test fun roles_should_fail_for_any_role_any_with_no_user() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, null,
                "app.users.rolesAny",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>(msg = "Unable to authorize, authorization provider not set")
        )
    }


    @Test fun roles_should_work_for_a_specific_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"),
                "app.users.rolesSpecific",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("rolesSpecific", msg="1 abc")
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "ops"),
                "app.users.rolesSpecific",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>("unauthorized")
        )
    }


    @Test fun roles_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "admin"),
                "app.users.rolesParent",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                success("rolesParent", msg="1 abc")
        )
    }


    @Test fun roles_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeAppRole, Pair("kishore", "dev"),
                "app.users.rolesParent",
                listOf( Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>("unauthorized")
        )
    }

    // ===================================================================
    //describe( "Authorization: using Key roles on actions" ) {
    @Test fun roles_by_key_should_work_when_role_is_any() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "dev"),
                "app.users.rolesAny",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")),
                success("rolesAny", msg="1 abc")
        )
    }


    @Test fun roles_by_key_should_fail_for_any_role_with_no_user() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeKeyRole, null,
                "app.users.rolesAny",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                null,
                unAuthorized<String>(msg = "Unable to authorize, authorization provider not set")
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "dev"),
                "app.users.rolesSpecific",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")),
                success("rolesSpecific", msg="1 abc")
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "qa"),
                "app.users.rolesSpecific",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "EB7EB37764AD4411A1763E6A593992BD")),
                unAuthorized<String>("unauthorized")
        )
    }


    @Test fun roles_by_key_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "admin"),
                "app.users.rolesParent",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "54B1817194C1450B886404C6BEA81673")),
                success("rolesParent", msg="1 abc")
        )
    }


    @Test fun roles_by_key_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        ensureCall(listOf(buildUserApiRegSingleton(ctx)),
                "*", "*", ApiConstants.AuthModeKeyRole, Pair("kishore", "dev"),
                "app.users.rolesParent",
                listOf(Pair("code", "1"), Pair("tag", "abc")),
                listOf(Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")),
                unAuthorized<String>("unauthorized")
        )
    }
}