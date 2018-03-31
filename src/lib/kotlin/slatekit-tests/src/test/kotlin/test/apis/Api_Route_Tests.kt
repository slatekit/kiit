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
import slatekit.apis.core.Api
import slatekit.apis.core.Auth
import slatekit.apis.core.Routes
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.helpers.ApiLoader
import slatekit.common.ApiKey
import slatekit.common.Result
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.common.results.ResultFuncs.success
import slatekit.entities.core.Entities
import slatekit.integration.common.AppEntContext
import test.setup.*

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Route_Tests : ApiTestsBase() {


    @Test fun can_setup_instance_with_declared_members_only() {
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
