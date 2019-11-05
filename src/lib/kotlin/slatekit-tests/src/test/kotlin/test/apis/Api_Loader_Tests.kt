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

import org.junit.Assert
import org.junit.Test
import slatekit.apis.*
import slatekit.apis.core.Api
import slatekit.apis.core.Sources
import slatekit.apis.core.Routes
import slatekit.apis.setup.AnnoLoader
import slatekit.apis.setup.MethodLoader
import slatekit.apis.setup.loadAll
import slatekit.apis.setup.toApi
import slatekit.common.Source
import slatekit.common.auth.Roles
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Loader_Tests : ApiTestsBase() {


    @Test fun can_load_api_from_annotations() {
        val api = AnnoLoader(SampleAnnoApi::class).loadApi(null)
        Assert.assertTrue(api.actions.size == 13)
        Assert.assertTrue(api.area == "app")
        Assert.assertTrue(api.name == "tests")
        Assert.assertTrue(api.desc == "sample to test features of Slate Kit APIs")
        Assert.assertTrue(api.roles.contains("admin"))
        Assert.assertTrue(api.auth == AuthMode.Token)
        Assert.assertTrue(api.verb == Verb.Auto)
        Assert.assertTrue(api.protocol == Source.All)
        Assert.assertTrue(api.actions.items[0].name == "inputBasicTypes")
        Assert.assertTrue(api.actions.items[0].params.size == 8)
        Assert.assertTrue(api.actions.items[0].paramsUser.size == 8)
    }


    @Test fun can_load_api_from_annotations_with_defaults() {
        val api = AnnoLoader(SampleApi::class).loadApi(null)
        Assert.assertTrue(api.actions.size == 1)
        Assert.assertTrue(api.area == "app")
        Assert.assertTrue(api.name == "tests")
        Assert.assertTrue(api.desc == "sample to test features of Slate Kit APIs")
        Assert.assertTrue(api.roles.contains("admin"))
        Assert.assertTrue(api.auth == AuthMode.Token)
        Assert.assertTrue(api.verb == Verb.Auto)
        Assert.assertTrue(api.protocol == Source.All)

        val action = api.actions.items[0]
        Assert.assertTrue(action.name == "defaultAnnotationValues")
        Assert.assertTrue(action.protocol == api.protocol)
        Assert.assertTrue(action.verb == Verb.Post)
        Assert.assertTrue(action.roles == api.roles)
        Assert.assertTrue(action.params.size == 1)
        Assert.assertTrue(action.paramsUser.size == 1)
    }


    @Test fun can_load_api_from_annotations_verb_mode_auto() {
        val api = AnnoLoader(SampleRESTVerbModeAutoApi::class).loadApi(null)
        Assert.assertTrue(api.actions.size == 8)
        Assert.assertTrue(api.area == "samples")
        Assert.assertTrue(api.name == "restVerbAuto")
        Assert.assertTrue(api.desc == "sample api for testing verb mode with auto")
        Assert.assertTrue(api.roles.contains(Roles.all))
        Assert.assertTrue(api.auth == AuthMode.Token)
        Assert.assertTrue(api.verb == Verb.Auto)
        Assert.assertTrue(api.protocol == Source.All)

        val actions = api.actions.items.map { Pair(it.name, it) }.toMap()
        Assert.assertEquals(Verb.Get  , actions[SampleRESTVerbModeAutoApi::getAll.name]!!.verb)
        Assert.assertEquals(Verb.Get  , actions[SampleRESTVerbModeAutoApi::getById.name]!!.verb)
        Assert.assertEquals(Verb.Post  , actions[SampleRESTVerbModeAutoApi::create.name]!!.verb)
        Assert.assertEquals(Verb.Put   , actions[SampleRESTVerbModeAutoApi::update.name]!!.verb)
        Assert.assertEquals(Verb.Patch , actions[SampleRESTVerbModeAutoApi::patch.name]!!.verb)
        Assert.assertEquals(Verb.Delete, actions[SampleRESTVerbModeAutoApi::delete.name]!!.verb)
        Assert.assertEquals(Verb.Delete, actions[SampleRESTVerbModeAutoApi::deleteById.name]!!.verb)
        Assert.assertEquals(Verb.Post  , actions[SampleRESTVerbModeAutoApi::activateById.name]!!.verb)
    }


    /**
     * POKO : Plain Old Kotlin Object
     * No annotations
     */
    @Test fun can_load_api_from_public_methods() {
        val api = MethodLoader(toApi(SampleExtendedApi::class,
                "app", "sampleExtended", "sample using plain kotlin class",
                roles = slatekit.apis.core.Roles(listOf("users")), local = true, auth = AuthMode.Token, protocol = Sources(listOf(Source.All)))).loadApi(null)

        Assert.assertTrue(api.actions.size == 2)
        Assert.assertTrue(api.area == "app")
        Assert.assertTrue(api.name == "sampleExtended")
        Assert.assertTrue(api.desc == "sample using plain kotlin class")
        Assert.assertTrue(api.roles.contains("users"))
        Assert.assertTrue(api.auth == AuthMode.Token)
        Assert.assertTrue(api.verb == Verb.Auto)
        Assert.assertTrue(api.protocol ==  Source.All)
    }


    /**
     * POKO : Plain Old Kotlin Object
     * No annotations
     */
    @Test fun can_load_api_from_public_methods_inherited() {
        val api = MethodLoader(toApi(SampleExtendedApi::class,
                "app", "sampleExtended", "sample using plain kotlin class",
                roles = slatekit.apis.core.Roles(listOf("users")), local = false, auth = AuthMode.Token, protocol = Sources(listOf(Source.All)))).loadApi(null)

        Assert.assertTrue(api.actions.size == 8)
        Assert.assertTrue(api.area == "app")
        Assert.assertTrue(api.name == "sampleExtended")
        Assert.assertTrue(api.desc == "sample using plain kotlin class")
        Assert.assertTrue(api.roles.contains("users"))
        Assert.assertTrue(api.auth == AuthMode.Token)
        Assert.assertTrue(api.verb == Verb.Auto)
        Assert.assertTrue(api.protocol ==  Source.All)
    }


    /**
     * Load areas
     */
    @Test fun can_load_areas() {

        val areas = loadAll(listOf(
                Api(SampleRolesByApp::class, "app", "sampleRolesByApp", "sample roles by application auth", roles = slatekit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declaredOnly = true),
                Api(SampleRolesByKey::class, "app", "sampleRolesByKey", "sample roles by api-key", roles = slatekit.apis.core.Roles(listOf("users")), auth = AuthMode.Keyed, sources = Sources(listOf(Source.CLI)), declaredOnly = true),
                Api(SampleExtendedApi::class, "tests", "sampleExtended", "sample plain kotlin class", roles = slatekit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declaredOnly = false)
        ))
        Assert.assertTrue(areas.size == 2)
        Assert.assertTrue(areas.contains("app"))
        Assert.assertTrue(areas.contains("tests"))
        Assert.assertTrue(areas.get("app")?.apis?.size == 2)
        Assert.assertTrue(areas.get("tests")?.apis?.size == 1)
    }



    @Test fun can_load_routes() {
        val areas = loadAll(listOf(
                Api(SampleRolesByApp::class, "app", "sampleRolesByApp", "sample roles by application auth", roles = slatekit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declaredOnly = true),
                Api(SampleRolesByKey::class, "app", "sampleRolesByKey", "sample roles by api-key", roles = slatekit.apis.core.Roles(listOf("users")), auth = AuthMode.Keyed, sources = Sources(listOf(Source.CLI)), declaredOnly = true),
                Api(SampleExtendedApi::class, "tests", "sampleExtended", "sample plain kotlin class", roles = slatekit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declaredOnly = false)
        ))

        val routes = Routes(areas, null)
        Assert.assertTrue(routes.areas.size == 2)
        Assert.assertTrue(routes.contains("app"))
        Assert.assertTrue(routes.contains("tests"))

        // Declared locally in class
        Assert.assertTrue(routes.contains("tests", "sampleExtended", "ping"))

        // Inherited from super class
        Assert.assertTrue(routes.contains("tests", "sampleExtended","hello"))

        val api = routes.api("tests", "sampleExtended")
        Assert.assertTrue(api?.area == "tests")
        Assert.assertTrue(api?.name == "sampleExtended")
        Assert.assertTrue(api?.actions?.size == 8)
    }
}
