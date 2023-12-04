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
import kiit.apis.routes.MethodExecutor
import kiit.apis.setup.*
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_001_Loader_Tests : ApiTestsBase() {

    /**
     * x-version-action:v1,v2,v2
     */
    @Test fun can_load_routes_from_annotations_with_defaults() {
        val router = router(
            versions = listOf(
                global(version = "0", apis = listOf(
                    api(SampleAnnotatedApiWithDefaults::class, SampleAnnotatedApiWithDefaults())
                ))
            )
        )
        val apiOpt = router.api("tests", "defaults", "0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        val api = actions.api
        Assert.assertEquals(1, actions.size)
        Assert.assertEquals("tests", api.area)
        Assert.assertEquals("defaults", api.name)
        Assert.assertEquals("sample to test features of Slate Kit APIs", api.desc)
        Assert.assertEquals(true, api.roles.isEmpty)
        Assert.assertEquals(AuthMode.Keyed, api.auth)
        Assert.assertEquals(Verb.Auto, api.verb)
        Assert.assertEquals("0", api.version)
        Assert.assertEquals(Access.Public, api.access)
        Assert.assertEquals(true, api.sources.hasAPI())

        Assert.assertEquals("add", actions.items[0].route.action.name)
        Assert.assertEquals(true, actions.items[0].route.action.roles.isEmpty)
        Assert.assertEquals(AuthMode.Keyed, actions.items[0].route.action.auth)
        Assert.assertEquals(Verb.Post, actions.items[0].route.action.verb)
        Assert.assertEquals("0", actions.items[0].route.action.version)
        Assert.assertEquals(Access.Public, actions.items[0].route.action.access)
        Assert.assertEquals(true, actions.items[0].route.action.sources.hasAPI())
        Assert.assertEquals(2, (actions.items[0].handler as MethodExecutor).call.params.size)
        Assert.assertEquals(2, (actions.items[0].handler as MethodExecutor).call.paramsUser.size)
    }


    @Test fun can_load_routes_from_annotations_with_overrides() {
        val router = router(
            versions = listOf(
                global(version = "0", apis = listOf(
                    api(SampleAnnotatedApiWithOverrides::class, SampleAnnotatedApiWithOverrides())
                ))
            )
        )
        val apiOpt = router.api("tests", "overrides", "0", "1.0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        val api = actions.api

        Assert.assertEquals(1, actions.size)
        Assert.assertEquals("tests", api.area)
        Assert.assertEquals("overrides", api.name)
        Assert.assertEquals("sample to test features of Slate Kit APIs", api.desc)
        Assert.assertEquals(true, api.roles.contains("admin"))
        Assert.assertEquals(AuthMode.Token, api.auth)
        Assert.assertEquals(Verb.Auto, api.verb)
        Assert.assertEquals("1", api.version)
        Assert.assertEquals(Access.Internal, api.access)
        Assert.assertEquals(false, api.sources.hasAPI())
        Assert.assertEquals(true, api.sources.hasCLI())

        Assert.assertEquals("adder", actions.items[0].route.action.name)
        Assert.assertEquals(true, actions.items[0].route.action.roles.contains("user"))
        Assert.assertEquals(AuthMode.Keyed, actions.items[0].route.action.auth)
        Assert.assertEquals(Verb.Put, actions.items[0].route.action.verb)
        Assert.assertEquals("1", actions.items[0].route.action.version)
        Assert.assertEquals(Access.Public, actions.items[0].route.action.access)
        Assert.assertEquals(true, actions.items[0].route.action.sources.hasAPI())
        Assert.assertEquals(false, actions.items[0].route.action.sources.hasCLI())
        Assert.assertEquals(2, (actions.items[0].handler as MethodExecutor).call.params.size)
        Assert.assertEquals(2, (actions.items[0].handler as MethodExecutor).call.paramsUser.size)
    }


    @Test fun can_load_routes_and_check_contains() {
        val router = router(
            versions = listOf(
                global(version = "0", apis = listOf(
                    api(SampleAnnotatedApiWithDefaults::class , SampleAnnotatedApiWithDefaults()),
                    api(SampleAnnotatedApiWithOverrides::class, SampleAnnotatedApiWithOverrides())
                ))
            )
        )
        // Check defaults
        Assert.assertTrue(router.containsArea(area = "tests"))
        Assert.assertTrue(router.containsArea(area = "tests", globalVersion = "0"))
        Assert.assertTrue(router.containsApi(area = "tests", api = "defaults"))
        Assert.assertTrue(router.containsApi(area = "tests", api = "defaults", globalVersion = "0"))
        Assert.assertTrue(router.containsAction(verb = Verb.Post.name, area = "tests", api = "defaults", action = "add"))
        Assert.assertTrue(router.containsAction(verb = Verb.Post.name, area = "tests", api = "defaults", action = "add", globalVersion = "0"))

        // Check overrides
        Assert.assertTrue(router.containsArea(area = "tests"))
        Assert.assertTrue(router.containsArea(area = "tests", globalVersion = "0"))
        Assert.assertFalse(router.containsArea(area = "tests", globalVersion = "1"))
        Assert.assertFalse(router.containsApi(area = "tests", api = "overrides", globalVersion = "1", version = "1.0"))
        Assert.assertTrue(router.containsApi(area = "tests", api = "overrides", globalVersion = "0", version = "1.0"))
        Assert.assertFalse(router.containsApi(area = "tests", api = "overrides", globalVersion = "0", version = "0.0"))
        Assert.assertFalse(router.containsAction(verb = Verb.Post.name, area = "tests", api = "overrides", action = "add", globalVersion = "0", version = "1.0"))
        Assert.assertFalse(router.containsAction(verb = Verb.Post.name, area = "tests", api = "overrides", action = "add", globalVersion = "1", version = "1.0"))
        Assert.assertFalse(router.containsAction(verb = Verb.Post.name, area = "tests", api = "overrides", action = "add", globalVersion = "0", version = "1.1"))
        Assert.assertFalse(router.containsAction (verb = Verb.Put.name , area = "tests", api = "overrides", action = "add", globalVersion = "0", version = "1.1"))
        Assert.assertTrue(router.containsAction (verb = Verb.Put.name , area = "tests", api = "overrides", action = "adder", globalVersion = "0", version = "1.1"))
    }

    @Test fun can_load_routes_only_public_and_annotated() {
        val router = router(
            versions = listOf(
                global(version = "0", apis = listOf(
                    api(SampleAnnotatedTestApi::class , SampleAnnotatedTestApi())
                ))
            )
        )
        val apiOpt = router.api("tests", "loader", "0")
        Assert.assertNotNull(apiOpt)
        val actions = apiOpt!!
        val api = actions.api
        Assert.assertEquals(2, actions.size)
        Assert.assertEquals("tests", api.area)
        Assert.assertEquals("loader", api.name)
        Assert.assertTrue(router.containsAction(verb = Verb.Post.name, area = "tests", api = "loader", action = "hi1"))
        Assert.assertTrue(router.containsAction(verb = Verb.Post.name, area = "tests", api = "loader", action = "publicHi2"))
        Assert.assertFalse(router.containsAction(verb = Verb.Post.name, area = "tests", api = "loader", action = "protectedHi"))
        Assert.assertFalse(router.containsAction(verb = Verb.Post.name, area = "tests", api = "loader", action = "privateHi"))
        Assert.assertFalse(router.containsAction(verb = Verb.Post.name, area = "tests", api = "loader", action = "nonAnnotatedHi"))
    }


    @Test
    fun can_load_routes_via_configuration() {
        val json = JSON
        val api = Loader(null).config(SampleAnnotatedApiWithDefaults::class, SampleAnnotatedApiWithDefaults(), json)
        print(api)
    }

    val JSON = """
    {
          "area"     : "spaces",
          "name"     : "manage",
          "desc2"     : "rpc calls for managing spaces",
          "auth"      : "token",
          "roles"    : ["user"],
          "verb"     : "auto",
          "access"   : "public",
          "sources"  : ["all"],
          "version"  : "0",
          "tags"     : [],
          "actions"  : [
              {
                  "name"     : "create",
                  "desc"     : "Description here",
                  "auth"      : "@parent",
                  "roles"    : [],
                  "verb"     : "Post",
                  "access"   : "public",
                  "sources"  : ["all"],
                  "version"  : "1",
                  "tags"     : [],
                  "handler"  : { "type": "MethodExecutor", "target": { "method": "create" } },
                  "handler"  : { "type": "RouteForwarder", "target": { "globalVersion": "1", "path": "spaces/manage/create", "verb": "Post" } }
              }
          ]
    }
    """.trimIndent()
}
