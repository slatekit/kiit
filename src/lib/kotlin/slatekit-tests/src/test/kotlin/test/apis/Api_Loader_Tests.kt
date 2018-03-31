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
import slatekit.apis.CliProtocol
import slatekit.apis.helpers.ApiLoader
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Loader_Tests : ApiTestsBase() {


    @Test fun can_load_from_annotations() {
        val api = ApiLoader.load(SampleAnnoApi::class, null)
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
    @Test fun can_load_from_poko() {
        val api = ApiLoader.loadPure(SampleExtendedApi::class,
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
    @Test fun can_load_from_poko_inherited() {
        val api = ApiLoader.loadPure(SampleExtendedApi::class,
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
}
