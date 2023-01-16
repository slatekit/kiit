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
import slatekit.apis.routes.Api
import slatekit.apis.core.Sources
import slatekit.apis.routes.Routes
import slatekit.apis.setup.Annotations
import slatekit.apis.setup.Methods
import slatekit.apis.setup.loadAll
import slatekit.apis.setup.toApi
import kiit.common.Source
import kiit.common.auth.Roles
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_010_Route_Tests : ApiTestsBase() {


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
