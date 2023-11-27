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
import kiit.apis.routes.Api
import kiit.apis.core.Sources
import kiit.apis.routes.Routes
import kiit.apis.setup.loadAll
import kiit.common.Source
import test.setup.*


/*
    {
        "name"   : "create",
        "version": "1",
        "verb"   : "Post",
        "host"   : null,
        "path"   : "spaces/management/addWithOwner",
        "auth"   : [ { "name": "Authorization", "type": "jwt"   , "value": "@app.token.id" } ],
        "meta"   : [ { "name": "x-meta"       , "type": "xmeta" , "value": "@app.meta.all" } ],
        "data"   : [ { "name": "space"        , "type": "SpaceDto"     } ],
        "output" : { "type": "SignUpResultDto", "value": "single" },


        "version": "1",
        "name"   : "add",
        "verb"   : "post",
        "desc"   : "add 2 numbers",
        "auth"   : [ "@parent" ],
        "roles"  : "admin",
        "access" : "public",
        "sources": [ "api", "queue" ],
        "tags    : []


        "handler": {
            "form"  : "Executor",
            "type"  : "calculator",
            "name"  : "create",
        },
        "handler": {
            "form"  : "Forwarder",
            "area"  : { "version": 1, name: "math"       },
            "api"   : { "version": 1, name: "calculator" },
            "action": { "version": 1, name: "add" },
        }
    },
 */
class Api_010_Router_Tests : ApiTestsBase() {



    @Test fun can_load_routes() {
        val areas = loadAll(listOf(
                Api(SampleRolesByApp::class, "app", "sampleRolesByApp", "sample roles by application auth", roles = kiit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declared = true),
                Api(SampleRolesByKey::class, "app", "sampleRolesByKey", "sample roles by api-key", roles = kiit.apis.core.Roles(listOf("users")), auth = AuthMode.Keyed, sources = Sources(listOf(Source.CLI)), declared = true),
                Api(SampleExtendedApi::class, "tests", "sampleExtended", "sample plain kotlin class", roles = kiit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declared = false)
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
