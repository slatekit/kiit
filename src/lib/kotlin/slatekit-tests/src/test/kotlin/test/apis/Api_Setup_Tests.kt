package test.apis

import org.junit.Assert
import org.junit.Test
import slatekit.apis.ApiHost
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.common.CommonContext
import slatekit.common.db.DbType
import slatekit.integration.apis.InfoApi
import slatekit.integration.apis.VersionApi
import slatekit.orm.model
import slatekit.orm.orm
import slatekit.results.getOrElse
import test.setup.*


class Api_Setup_Tests : ApiTestsBase() {


    @Test fun can_setup_instance_as_new() {
        val apis = ApiHost(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")), allowIO = false)
        val result = apis.getApi("app", "SamplePOKO", "getTime" )

        val apiRef = result.getOrElse { null }
        Assert.assertTrue(result.success && apiRef?.instance is SamplePOKOApi)
        Assert.assertTrue((apiRef?.instance as SamplePOKOApi).count == 0)
    }


    @Test fun can_setup_instance_as_new_with_context() {
        ctx.ent.orm<Long, Movie>(DbType.DbTypeMemory, Long::class, Movie::class)
        val apis = ApiHost(ctx, apis = listOf(Api(SampleEntityApi::class, "app", "SampleEntity")), allowIO = false)
        val result = apis.getApi("app", "SampleEntity", "patch" )

        val apiRef = result.getOrElse { null }
        Assert.assertTrue(result.success && apiRef?.instance is SampleEntityApi)
    }


    @Test fun can_setup_instance_as_singleton() {
        val inst = SamplePOKOApi()
        inst.count = 1001
        val apis = ApiHost(ctx, apis = listOf(Api(inst, "app", "SamplePOKO")), allowIO = false)
        val result = apis.getApi("app", "SamplePOKO", "getTime" )
        val apiRef = result.getOrElse { null }
        Assert.assertTrue(result.success && apiRef?.instance is SamplePOKOApi)
        Assert.assertTrue((apiRef?.instance as SamplePOKOApi).count == 1001)
    }


    @Test fun can_setup_instance_with_declared_members_only() {
        val apis = ApiHost(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")), auth = null, allowIO = false)
        Assert.assertTrue( apis.getApi("app"   , "SamplePOKO", "getTime"    ).success)
        Assert.assertTrue( apis.getApi("app"   , "SamplePOKO", "getCounter" ).success)
        Assert.assertTrue( apis.getApi("app"   , "SamplePOKO", "hello"      ).success)
        Assert.assertTrue( apis.getApi("app"   , "SamplePOKO", "request"    ).success)
        Assert.assertTrue( apis.getApi("app"   , "SamplePOKO", "response"   ).success)
        Assert.assertTrue(!apis.getApi("app"   , "SamplePOKO", "getEmail"   ).success)
        Assert.assertTrue(!apis.getApi("app"   , "SamplePOKO", "getSsn"     ).success)
    }


    @Test fun can_setup_instance_with_inheritance() {
        val apis = ApiHost(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended", declaredOnly = false)), auth = null, allowIO = false)
        Assert.assertTrue( apis.getApi("app"   , "SampleExtended", "getSeconds" ).success)
        Assert.assertTrue( apis.getApi("app"   , "SampleExtended", "getTime"    ).success)
        Assert.assertTrue( apis.getApi("app"   , "SampleExtended", "getCounter" ).success)
        Assert.assertTrue( apis.getApi("app"   , "SampleExtended", "hello"      ).success)
        Assert.assertTrue( apis.getApi("app"   , "SampleExtended", "request"    ).success)
        Assert.assertTrue( apis.getApi("app"   , "SampleExtended", "response"   ).success)
        Assert.assertTrue(!apis.getApi("app"   , "SampleExtended", "getEmail"   ).success)
        Assert.assertTrue(!apis.getApi("app"   , "SampleExtended", "getSsn"     ).success)
    }


    @Test fun can_setup_instance_with_compositional_apis_with_annotations() {
        ctx.ent.orm<Long, Movie>(DbType.DbTypeMemory, Long::class, Movie::class)
        val apis = ApiHost(ctx, apis = listOf(Api(SampleEntity2Api::class, declaredOnly = false, setup = Annotated)),
                auth = null, allowIO = false)
        Assert.assertTrue( apis.getApi("app"   , "tests", "patch" ).success)
        Assert.assertTrue( apis.getApi("app"   , "tests", "recent" ).success)
        Assert.assertTrue( apis.getApi("app"   , "tests", "deleteById" ).success)
    }


    @Test fun can_check_action_does_NOT_exist() {
        val apis = ApiHost(ctx, apis = listOf(
                Api(SamplePOKOApi::class, "app", "SamplePOKO"),
                Api(InfoApi(ctx), setup = Annotated),
                Api(VersionApi(ctx), setup = Annotated)
        ), auth = null, allowIO = false)

        Assert.assertTrue(!apis.routes.contains("app.SamplePOKO.fakeMethod"))
        Assert.assertTrue(!apis.routes.contains("sys.app.host2"))
    }


    @Test fun can_check_action_exists() {
        val apis = ApiHost(ctx, apis = listOf(
                Api(SamplePOKOApi::class, "app", "SamplePOKO"),
                Api(InfoApi(ctx), setup = Annotated),
                Api(VersionApi(ctx), setup = Annotated)
        ), auth = null, allowIO = false)

        Assert.assertTrue(apis.routes.check("app.SamplePOKO.getCounter"))
        Assert.assertTrue(apis.routes.check("app.info.about"))
    }


    @Test fun can_call_action_without_area() {
        val apis = ApiHost(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")), auth = null, allowIO = false)
        val result = apis.call("app", "SamplePOKO", "getCounter", "", mapOf(), mapOf())
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } == 1)
    }


    @Test fun can_call_action_in_derived_class() {
        val apis = ApiHost(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended")), auth = null, allowIO = false)
        val result = apis.call("app", "SampleExtended", "getSeconds", "", mapOf(), mapOf())
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { -1 } in 0..59)
    }


    @Test fun can_call_action_in_base_class() {
        val apis = ApiHost(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended", declaredOnly = false)), auth = null, allowIO = false)
        val result = apis.call("app", "SampleExtended", "getCounter", "", mapOf(), mapOf())
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } == 1)
    }


    @Test
    fun can_get_api_info_from_method() {
        val ctx = CommonContext.simple("queues")
        val api = WorkerSampleApi(ctx)
        val apis = ApiHost(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null , allowIO = false)
        val apiRef = apis.getApi(WorkerSampleApi::class, WorkerSampleApi::test1)
        Assert.assertTrue( apiRef.getOrElse { null }?.api?.area == "samples")
        Assert.assertTrue( apiRef.getOrElse { null }?.api?.name == "workerqueue")
        Assert.assertTrue( apiRef.getOrElse { null }?.action?.name == "test1")
    }
}
