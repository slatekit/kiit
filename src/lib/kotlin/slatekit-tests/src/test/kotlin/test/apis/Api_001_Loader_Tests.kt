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

import org.junit.Assert
import org.junit.Test
import kiit.apis.*
import kiit.apis.routes.Actions
import kiit.apis.routes.MethodExecutor
import kiit.apis.setup.*
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_001_Loader_Tests : ApiTestsBase() {


    @Test
    fun can_load_routes_from_annotations_with_defaults() {
        val router = router(
            versions = listOf(
                global(
                    version = "0", apis = listOf(
                        api(SampleAnnotatedApiWithDefaults::class, SampleAnnotatedApiWithDefaults())
                    )
                )
            )
        )
        val apiOpt = router.getApi("tests", "defaults", "0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        ensureDefaultSetup(actions)
    }

    @Test
    fun can_load_routes_from_config_with_defaults() {
        val router = router(
            versions = listOf(
                global(
                    version = "0", apis = listOf(
                        api(
                            SampleAnnotatedApiWithDefaults::class,
                            SampleAnnotatedApiWithDefaults(),
                            setup = SetupType.Config,
                            content = JSON_DEFAULTS.trim()
                        )
                    )
                )
            )
        )
        val apiOpt = router.getApi("tests", "defaults", "0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        ensureDefaultSetup(actions)
    }


    private fun ensureDefaultSetup(actions: Actions) {
        val api = actions.api
        Assert.assertEquals(1, actions.size)
        Assert.assertEquals("tests", api.area)
        Assert.assertEquals("defaults", api.name)
        Assert.assertEquals("sample to test features of Kiit APIs", api.desc)
        Assert.assertEquals(true, api.roles.isEmpty)
        Assert.assertEquals(AuthMode.Keyed, api.auth)
        Assert.assertEquals(Verb.Auto, api.verb)
        Assert.assertEquals("0", api.version)
        Assert.assertEquals(Access.Public, api.access)
        Assert.assertEquals(true, api.sources.hasAPI())

        Assert.assertEquals("add", actions.items[0].path.action.name)
        Assert.assertEquals(true , actions.items[0].path.action.roles.isEmpty)
        Assert.assertEquals(AuthMode.Keyed, actions.items[0].path.action.auth)
        Assert.assertEquals(Verb.Post     , actions.items[0].path.action.verb)
        Assert.assertEquals("0"  , actions.items[0].path.action.version)
        Assert.assertEquals(Access.Public , actions.items[0].path.action.access)
        Assert.assertEquals(true , actions.items[0].path.action.sources.hasAPI())
        Assert.assertEquals(2    , (actions.items[0].handler as MethodExecutor).call.params.size)
    }


    @Test
    fun can_load_routes_from_annotations_with_overrides() {
        val router = router(
            versions = listOf(
                global(
                    version = "0", apis = listOf(
                        api(SampleAnnotatedApiWithOverrides::class, SampleAnnotatedApiWithOverrides())
                    )
                )
            )
        )
        val apiOpt = router.getApi("tests", "overrides", "0", "1.0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        ensureOverrideSetup(actions)
    }


    @Test
    fun can_load_routes_from_config_with_overrides() {
        val router = router(
            versions = listOf(
                global(
                    version = "0", apis = listOf(
                        api(
                            SampleApiWithConfigSetup::class,
                            SampleApiWithConfigSetup(),
                            setup = SetupType.Config,
                            content = JSON_OVERRIDES.trim()
                        )
                    )
                )
            )
        )
        val apiOpt = router.getApi("tests", "overrides", "0", "1.0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        ensureOverrideSetup(actions)
    }


    fun ensureOverrideSetup(actions: Actions) {
        val api = actions.api
        Assert.assertEquals(1, actions.size)
        Assert.assertEquals("tests", api.area)
        Assert.assertEquals("overrides", api.name)
        Assert.assertEquals("sample to test features of Kiit APIs", api.desc)
        Assert.assertEquals(true, api.roles.contains("admin"))
        Assert.assertEquals(AuthMode.Token, api.auth)
        Assert.assertEquals(Verb.Auto, api.verb)
        Assert.assertEquals("1", api.version)
        Assert.assertEquals(Access.Internal, api.access)
        Assert.assertEquals(false, api.sources.hasAPI())
        Assert.assertEquals(true, api.sources.hasCLI())
        Assert.assertEquals("apiTag1", api.tags[0])
        Assert.assertEquals("apiTag2", api.tags[1])
        Assert.assertEquals("apiPolicy1", api.policies[0])
        Assert.assertEquals("apiPolicy2", api.policies[1])

        val action = actions.items[0].path.action
        Assert.assertEquals("adder", actions.items[0].path.action.name)
        Assert.assertEquals(true, actions.items[0].path.action.roles.contains("user"))
        Assert.assertEquals(AuthMode.Keyed, actions.items[0].path.action.auth)
        Assert.assertEquals(Verb.Put, actions.items[0].path.action.verb)
        Assert.assertEquals("1", actions.items[0].path.action.version)
        Assert.assertEquals(Access.Public, actions.items[0].path.action.access)
        Assert.assertEquals(true, actions.items[0].path.action.sources.hasAPI())
        Assert.assertEquals(false, actions.items[0].path.action.sources.hasCLI())
        Assert.assertEquals(2, (actions.items[0].handler as MethodExecutor).call.params.size)
        Assert.assertEquals("actionTag1", action.tags[0])
        Assert.assertEquals("actionTag2", action.tags[1])
        Assert.assertEquals("actionPolicy1", action.policies[0])
        Assert.assertEquals("actionPolicy2", action.policies[1])
    }


    @Test
    fun can_load_routes_with_redirects() {
        val router = router(
            versions = listOf(
                global(
                    version = "0", apis = listOf(
                        api(
                            SampleApiWithConfigSetup::class,
                            SampleApiWithConfigSetup(),
                            setup = SetupType.Config,
                            content = JSON_REDIRECTS.trim()
                        )
                    )
                )
            )
        )
        val actionOpt = router.getAction(Verbs.POST, "tests", "redirects", "adder", version = null)
        Assert.assertNotNull(actionOpt)
        val actions = actionOpt!!
        Assert.assertEquals("tests", actions.path.area.name)
        Assert.assertEquals("redirects", actions.path.api.name)
        Assert.assertEquals("add", actions.path.action.name)
        Assert.assertEquals(Verb.Post, actions.path.action.verb)
    }


    @Test
    fun can_load_routes_and_check_contains() {
        val router = router(
            versions = listOf(
                global(
                    version = "0", apis = listOf(
                        api(SampleAnnotatedApiWithDefaults::class, SampleAnnotatedApiWithDefaults()),
                        api(SampleAnnotatedApiWithOverrides::class, SampleAnnotatedApiWithOverrides())
                    )
                )
            )
        )
        // Check defaults
        Assert.assertTrue(router.containsArea(area = "tests"))
        Assert.assertTrue(router.containsArea(area = "tests", globalVersion = "0"))
        Assert.assertTrue(router.containsApi(area = "tests", api = "defaults"))
        Assert.assertTrue(router.containsApi(area = "tests", api = "defaults", globalVersion = "0"))
        Assert.assertTrue(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "defaults",
                action = "add"
            )
        )
        Assert.assertTrue(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "defaults",
                action = "add",
                globalVersion = "0"
            )
        )

        // Check overrides
        Assert.assertTrue(router.containsArea(area = "tests"))
        Assert.assertTrue(router.containsArea(area = "tests", globalVersion = "0"))
        Assert.assertFalse(router.containsArea(area = "tests", globalVersion = "1"))
        Assert.assertFalse(router.containsApi(area = "tests", api = "overrides", globalVersion = "1", version = "1.0"))
        Assert.assertTrue(router.containsApi(area = "tests", api = "overrides", globalVersion = "0", version = "1.0"))
        Assert.assertFalse(router.containsApi(area = "tests", api = "overrides", globalVersion = "0", version = "0.0"))
        Assert.assertFalse(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "overrides",
                action = "add",
                globalVersion = "0",
                version = "1.0"
            )
        )
        Assert.assertFalse(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "overrides",
                action = "add",
                globalVersion = "1",
                version = "1.0"
            )
        )
        Assert.assertFalse(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "overrides",
                action = "add",
                globalVersion = "0",
                version = "1.1"
            )
        )
        Assert.assertFalse(
            router.containsAction(
                verb = Verb.Put.name,
                area = "tests",
                api = "overrides",
                action = "add",
                globalVersion = "0",
                version = "1.1"
            )
        )
        Assert.assertTrue(
            router.containsAction(
                verb = Verb.Put.name,
                area = "tests",
                api = "overrides",
                action = "adder",
                globalVersion = "0",
                version = "1.1"
            )
        )
    }

    @Test
    fun can_load_routes_only_public_and_annotated() {
        val router = router(
            versions = listOf(
                global(
                    version = "0", apis = listOf(
                        api(SampleAnnotatedTestApi::class, SampleAnnotatedTestApi())
                    )
                )
            )
        )
        val apiOpt = router.getApi("tests", "loader", "0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        val api = actions.api
        Assert.assertEquals(2, actions.size)
        Assert.assertEquals("tests", api.area)
        Assert.assertEquals("loader", api.name)
        Assert.assertTrue(router.containsAction(verb = Verb.Post.name, area = "tests", api = "loader", action = "hi1"))
        Assert.assertTrue(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "loader",
                action = "publicHi2"
            )
        )
        Assert.assertFalse(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "loader",
                action = "protectedHi"
            )
        )
        Assert.assertFalse(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "loader",
                action = "privateHi"
            )
        )
        Assert.assertFalse(
            router.containsAction(
                verb = Verb.Post.name,
                area = "tests",
                api = "loader",
                action = "nonAnnotatedHi"
            )
        )
    }


    @Test
    fun can_load_routes_via_configuration() {
        val json = JSON_DEFAULTS
        val api = Loader(null).config(SampleApiWithConfigSetup::class, SampleApiWithConfigSetup(), json)
        print(api)
    }

    companion object {

        val JSON_DEFAULTS = """
    {   
          "area"     : "tests",
          "name"     : "defaults",
          "desc"     : "sample to test features of Kiit APIs",
          "auth"     : "keyed",
          "roles"    : [],
          "verb"     : "auto",
          "access"   : "public",
          "sources"  : ["all"],
          "version"  : "0",
          "policies" : [],
          "tags"     : [],
          "actions"  : [
              {
                  "execute"  :  { "type": "method", "target": "add" }
              }
          ]
    }
    """.trimIndent()

        val JSON_OVERRIDES = """
    {
          "area"     : "tests",
          "name"     : "overrides",
          "desc"     : "sample to test features of Kiit APIs",
          "auth"     : "token",
          "roles"    : ["admin"],
          "verb"     : "auto",
          "access"   : "internal",
          "sources"  : ["cli"],
          "version"  : "1",
          "policies" : ["apiPolicy1", "apiPolicy2"],
          "tags"     : ["apiTag1", "apiTag2"],
          "actions"  : [
              {
                  "name"     : "adder",
                  "desc"     : "accepts supplied basic data types from send",
                  "auth"     : "keyed",
                  "roles"    : ["user"],
                  "verb"     : "put",
                  "access"   : "public",
                  "sources"  : ["api"],
                  "version"  : "1",
                  "policies" : ["actionPolicy1", "actionPolicy2"],
                  "tags"     : ["actionTag1", "actionTag2"],
                  "execute"  :  { "type": "method", "target": "add" }
              }
          ]
    }
    """.trimIndent()

        val JSON_REDIRECTS = """
    {   
          "area"     : "tests",
          "name"     : "redirects",
          "desc"     : "sample to test features of Kiit APIs",
          "auth"     : "keyed",
          "roles"    : [],
          "verb"     : "auto",
          "access"   : "public",
          "sources"  : ["all"],
          "version"  : "0",
          "tags"     : [],
          "actions"  : [
              {
                  "name"     : "adder",
                  "verb"     : "post",
                  "execute"  :  { "type": "redirect", "target": "tests/redirects/add", "globalVersion": "0", "verb": "post" }
              },
              {
                  "execute"  :  { "type": "method", "target": "add" }
              }
          ]
    }
    """.trimIndent()
    }
}
