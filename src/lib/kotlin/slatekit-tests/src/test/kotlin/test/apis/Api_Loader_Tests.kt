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
import slatekit.apis.security.CliProtocol
import slatekit.apis.core.Api
import slatekit.apis.core.Routes
import slatekit.apis.helpers.ApiLoader
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Loader_Tests : ApiTestsBase() {


    @Test fun can_load_api_from_annotations() {
        val api = ApiLoader.loadAnnotated(SampleAnnoApi::class, null)
        assert(api.actions.size == 13)
        assert(api.area == "app")
        assert(api.name == "tests")
        assert(api.desc == "sample to test features of Slate Kit APIs")
        assert(api.roles == "admin")
        assert(api.auth == "app-roles")
        assert(api.verb == "*")
        assert(api.protocol == "*")
        assert(api.actions.items[0].name == "inputBasicTypes")
        assert(api.actions.items[0].params.size == 8)
        assert(api.actions.items[0].paramsUser.size == 8)
    }


    /**
     * POKO : Plain Old Kotlin Object
     * No annotations
     */
    @Test fun can_load_api_from_public_methods() {
        val api = ApiLoader.loadPublic(SampleExtendedApi::class,
            "app", "sampleExtended", "sample using plain kotlin class",
            true, "users", "app-roles", "*",
            CliProtocol.name, true, null)

        assert(api.actions.size == 2)
        assert(api.area == "app")
        assert(api.name == "sampleExtended")
        assert(api.desc == "sample using plain kotlin class")
        assert(api.roles == "users")
        assert(api.auth == "app-roles")
        assert(api.verb == "*")
        assert(api.protocol == CliProtocol.name)
    }


    /**
     * POKO : Plain Old Kotlin Object
     * No annotations
     */
    @Test fun can_load_api_from_public_methods_inherited() {
        val api = ApiLoader.loadPublic(SampleExtendedApi::class,
            "app", "sampleExtended", "sample using plain kotlin class",
            false, "users", "app-roles", "*",
            CliProtocol.name, true, null)

        assert(api.actions.size == 8)
        assert(api.area == "app")
        assert(api.name == "sampleExtended")
        assert(api.desc == "sample using plain kotlin class")
        assert(api.roles == "users")
        assert(api.auth == "app-roles")
        assert(api.verb == "*")
        assert(api.protocol == CliProtocol.name)
    }


    /**
     * Load using supplied metadata
     */
    @Test fun can_load_api_from_supplied_meta() {
        val api = ApiLoader.loadWithMeta(
            Api(SampleExtendedApi::class,
            "app", "sampleExtended", "sample using plain kotlin class",
            "users", "app-roles", "*",
            CliProtocol.name, true, null), null)

        assert(api.actions.size == 2)
        assert(api.area == "app")
        assert(api.name == "sampleExtended")
        assert(api.desc == "sample using plain kotlin class")
        assert(api.roles == "users")
        assert(api.auth == "app-roles")
        assert(api.verb == "*")
        assert(api.protocol == CliProtocol.name)
    }


    /**
     * Load areas
     */
    @Test fun can_load_areas() {

        val areas = ApiLoader.loadAll(listOf(
            Api(SampleRolesByApp::class,
                "app", "sampleExtended", "sample roles by application auth",
                "users", "app-roles", "*",
                CliProtocol.name, true, null),

            Api(SampleRolesByKey::class,
                "app", "sampleExtended", "sample roles by api-key",
                "users", "key-roles", "*",
                CliProtocol.name, true, null),

            Api(SampleExtendedApi::class,
                "tests", "sampleExtended", "sample using plain kotlin class",
                "users", "app-roles", "*",
                CliProtocol.name, true, null)
        ))

        assert(areas.size == 2)
        assert(areas.contains("app"))
        assert(areas.contains("tests"))
        assert(areas.get("app")?.apis?.size == 2)
        assert(areas.get("tests")?.apis?.size == 1)
    }



    @Test fun can_load_routes() {
        val areas = ApiLoader.loadAll(listOf(
            Api(SampleRolesByApp::class,
                "app", "sampleRolesByApp", "sample roles by application auth",
                "users", "app-roles", "*",
                CliProtocol.name, true, null),

            Api(SampleRolesByKey::class,
                "app", "sampleRolesByKey", "sample roles by api-key",
                "users", "key-roles", "*",
                CliProtocol.name, true, null),

            Api(SampleExtendedApi::class,
                "tests", "sampleExtended", "sample using plain kotlin class",
                "users", "app-roles", "*",
                CliProtocol.name, false, null)
        ))

        val routes = Routes(areas, null)
        assert(routes.areas.size == 2)
        assert(routes.contains("app"))
        assert(routes.contains("tests"))

        // Declared locally in class
        assert(routes.contains("tests", "sampleExtended", "ping"))

        // Inherited from super class
        assert(routes.contains("tests", "sampleExtended","hello"))

        val api = routes.api("tests", "sampleExtended")
        assert(api?.area == "tests")
        assert(api?.name == "sampleExtended")
        assert(api?.actions?.size == 8)
    }
}
