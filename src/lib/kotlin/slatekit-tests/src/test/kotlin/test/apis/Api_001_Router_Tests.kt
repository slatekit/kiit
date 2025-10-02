package test.apis

import kiit.apis.setup.api
import kiit.apis.setup.router
import kiit.context.AppContext
import org.junit.Test
import test.setup.SampleApi1V1
import test.setup.SampleApi2V2


class Api_001_Router_Tests : ApiTestsBase() {

    @Test
    fun can_load_routes_with_versions() {
        val context = AppContext.simple(Api_001_Router_Tests::class.java, "simple")
        val router = router(listOf(
                api(SampleApi1V1::class, SampleApi1V1(context)),
                api(SampleApi2V2::class, SampleApi2V2(context))
            )
        )

        router.routes.items.forEachIndexed { ndx, area ->
            println("Version ndx = $ndx, name = ${area.name}")
            area.items.forEachIndexed { ndxApi, api ->
                println("- Area  ndx = $ndxApi, version = ${api.api.version}, name = ${api.api.name}")
                api.items.forEachIndexed { ndxAction, action ->
                    println("  - Action  ndx = $ndxAction, version = ${action.action.version}, name = ${action.action.name}, ")
                }
            }
        }
    }
}