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
import kiit.apis.setup.Annotations
import kiit.apis.setup.Methods
import kiit.apis.setup.loadAll
import kiit.apis.setup.toApi
import kiit.common.Source
import kiit.common.auth.Roles
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_010_Route_Tests : ApiTestsBase() {


    @Test fun can_load_routes() {
        val areas = loadAll(listOf(
                Api(SampleRolesByApp::class, "app", "sampleRolesByApp", "sample roles by application auth", roles = kiit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declaredOnly = true),
                Api(SampleRolesByKey::class, "app", "sampleRolesByKey", "sample roles by api-key", roles = kiit.apis.core.Roles(listOf("users")), auth = AuthMode.Keyed, sources = Sources(listOf(Source.CLI)), declaredOnly = true),
                Api(SampleExtendedApi::class, "tests", "sampleExtended", "sample plain kotlin class", roles = kiit.apis.core.Roles(listOf("users")), auth = AuthMode.Token, sources = Sources(listOf(Source.CLI)), declaredOnly = false)
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
