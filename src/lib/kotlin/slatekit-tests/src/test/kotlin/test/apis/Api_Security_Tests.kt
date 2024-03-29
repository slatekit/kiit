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

import org.junit.Test
import kiit.apis.routes.Api
import kiit.apis.SetupType
import kiit.apis.Verbs
import kiit.apis.setup.GlobalVersion
import kiit.apis.setup.api
import kiit.apis.setup.routes
import kiit.common.info.Credentials
import kiit.requests.CommonRequest
import kiit.common.Source
import kiit.requests.toResponse
import kiit.results.Success
import kiit.results.builders.Notices
import kiit.results.builders.Outcomes
import test.apis.samples.Sample_API_1_Core
import test.apis.samples.Sample_API_2_Roles

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Security_Tests : ApiTestsBase() {

    // ===================================================================
    //describe( "Authorization: using App roles on actions" ) {
    @Test
    fun roles_should_work_when_role_is_any() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.rolesAny", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("rolesAny", msg = "1 abc").toResponse()
        )

        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "qa"),
            request = CommonRequest.path(
                "app.rolesTest.rolesAny", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("rolesAny", msg = "1 abc").toResponse()
        )

        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = ""),
            request = CommonRequest.path(
                "app.rolesTest.rolesAny", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Outcomes.denied<Any>("unauthorized").toResponse()
        )
    }


    @Test
    fun roles_should_fail_for_any_role_any_with_no_user() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = null,
            request = CommonRequest.path(
                "app.rolesTest.rolesAny", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Notices.denied<String>(msg = "Unable to authorize, authorization provider not set").toResponse()
        )
    }


    @Test
    fun roles_should_work_for_a_specific_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.rolesSpecific", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("rolesSpecific", msg = "1 abc").toResponse()
        )
    }


    @Test
    fun roles_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "ops"),
            request = CommonRequest.path(
                "app.rolesTest.rolesSpecific", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Notices.denied<String>("unauthorized").toResponse()
        )
    }


    @Test
    fun roles_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "admin"),
            request = CommonRequest.path(
                "app.rolesTest.rolesParent", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("rolesParent", msg = "1 abc").toResponse()
        )
    }


    @Test
    fun roles_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.rolesParent", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Notices.denied<String>("unauthorized").toResponse()
        )
    }

    // ===================================================================
    //describe( "Authorization: using Key roles on actions" ) {
    @Test
    fun roles_by_key_should_work_when_role_is_any() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.rolesAny", Verbs.POST, mapOf(
                    Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("rolesAny", msg = "1 abc").toResponse()
        )
    }


    @Test
    fun roles_by_key_should_fail_for_any_role_with_no_user() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = null,
            request = CommonRequest.path(
                "app.rolesTest.rolesAny", Verbs.POST, mapOf(), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Notices.denied<String>(msg = "Unable to authorize, authorization provider not set").toResponse()
        )
    }


    @Test
    fun roles_by_key_should_work_for_a_specific_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.rolesSpecific", Verbs.POST, mapOf(
                    Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("rolesSpecific", msg = "1 abc").toResponse()
        )
    }


    @Test
    fun roles_by_key_should_fail_for_a_specific_role_when_user_has_a_different_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "qa"),
            request = CommonRequest.path(
                "app.rolesTest.rolesSpecific", Verbs.POST, mapOf(
                    Pair("api-key", "EB7EB37764AD4411A1763E6A593992BD")
                ), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Notices.denied<String>("unauthorized").toResponse()
        )
    }


    @Test
    fun roles_by_key_should_work_for_a_specific_role_when_referring_to_its_parent_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "admin"),
            request = CommonRequest.path(
                "app.rolesTest.rolesParent", Verbs.POST, mapOf(
                    Pair("api-key", "54B1817194C1450B886404C6BEA81673")
                ), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("rolesParent", msg = "1 abc").toResponse()
        )
    }


    @Test
    fun roles_by_key_should_fail_for_a_specific_role_when_referring_to_its_parent_role_when_user_has_a_different_role() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.rolesParent", Verbs.POST, mapOf(
                    Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Notices.denied<String>("unauthorized").toResponse()
        )
    }


    @Test
    fun should_use_action_auth_as_override() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.authOverride", Verbs.POST, mapOf(
                    Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD")
                ), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Success("authOverride", msg = "1 abc").toResponse()
        )
    }


    @Test
    fun should_use_action_auth_as_override_fails_with_bad_key() {
        checkCall(
            protocol = Source.All,
            routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_2_Roles::class, Sample_API_2_Roles()))))),
            user = Credentials(name = "kishore", roles = "dev"),
            request = CommonRequest.path(
                "app.rolesTest.authOverride", Verbs.POST, mapOf(
                    Pair("api-key", "3E35584A8DE0460BB28D6E0D32FB4CFD_INCORRECT")
                ), mapOf(
                    Pair("code", "1"),
                    Pair("tag", "abc")
                )
            ),
            response = Notices.errored<String>("unauthorized").toResponse()
        )
    }
}
