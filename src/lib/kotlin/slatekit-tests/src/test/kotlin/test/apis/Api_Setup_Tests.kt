package test.apis

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.apis.ApiServer
import slatekit.apis.Verb
import slatekit.apis.routes.Api
import slatekit.apis.SetupType
import slatekit.context.AppContext
import slatekit.common.data.Vendor
import slatekit.integration.apis.InfoApi
import slatekit.integration.apis.VersionApi
import slatekit.orm.orm
import slatekit.results.getOrElse
import test.TestApp
import test.setup.*


class Api_Setup_Tests : ApiTestsBase() {


    @Test fun can_setup_instance_as_new() {
        val apis = ApiServer(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")))
        val result = apis.get("app", "SamplePOKO", "getTime" )

        val apiRef = result.getOrElse { null }
        Assert.assertTrue(result.success && apiRef?.instance is SamplePOKOApi)
        Assert.assertTrue((apiRef?.instance as SamplePOKOApi).count == 0)
    }


    @Test fun can_setup_instance_as_new_with_context() {
        ctx.ent.orm<Long, Movie>(Vendor.Memory, Long::class, Movie::class)
        val apis = ApiServer(ctx, apis = listOf(Api(SampleEntityApi::class, "app", "SampleEntity")))
        val result = apis.get("app", "SampleEntity", "patch" )

        val apiRef = result.getOrElse { null }
        Assert.assertTrue(result.success && apiRef?.instance is SampleEntityApi)
    }


    @Test fun can_setup_instance_as_singleton() {
        val inst = SamplePOKOApi()
        inst.count = 1001
        val apis = ApiServer(ctx, apis = listOf(Api(inst, "app", "SamplePOKO")))
        val result = apis.get("app", "SamplePOKO", "getTime" )
        val apiRef = result.getOrElse { null }
        Assert.assertTrue(result.success && apiRef?.instance is SamplePOKOApi)
        Assert.assertTrue((apiRef?.instance as SamplePOKOApi).count == 1001)
    }


    @Test fun can_setup_instance_with_declared_members_only() {
        val apis = ApiServer(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")))
        Assert.assertTrue( apis.get("app"   , "SamplePOKO", "getTime"    ).success)
        Assert.assertTrue( apis.get("app"   , "SamplePOKO", "getCounter" ).success)
        Assert.assertTrue( apis.get("app"   , "SamplePOKO", "hello"      ).success)
        Assert.assertTrue( apis.get("app"   , "SamplePOKO", "request"    ).success)
        Assert.assertTrue( apis.get("app"   , "SamplePOKO", "response"   ).success)
        Assert.assertTrue(!apis.get("app"   , "SamplePOKO", "getEmail"   ).success)
        Assert.assertTrue(!apis.get("app"   , "SamplePOKO", "getSsn"     ).success)
    }


    @Test fun can_setup_instance_with_inheritance() {
        val apis = ApiServer(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended", declaredOnly = false)))
        Assert.assertTrue( apis.get("app"   , "SampleExtended", "getSeconds" ).success)
        Assert.assertTrue( apis.get("app"   , "SampleExtended", "getTime"    ).success)
        Assert.assertTrue( apis.get("app"   , "SampleExtended", "getCounter" ).success)
        Assert.assertTrue( apis.get("app"   , "SampleExtended", "hello"      ).success)
        Assert.assertTrue( apis.get("app"   , "SampleExtended", "request"    ).success)
        Assert.assertTrue( apis.get("app"   , "SampleExtended", "response"   ).success)
        Assert.assertTrue(!apis.get("app"   , "SampleExtended", "getEmail"   ).success)
        Assert.assertTrue(!apis.get("app"   , "SampleExtended", "getSsn"     ).success)
    }


    @Test fun can_setup_instance_with_compositional_apis_with_annotations() {
        ctx.ent.orm<Long, Movie>(Vendor.Memory, Long::class, Movie::class)
        val apis = ApiServer(ctx, apis = listOf(Api(SampleEntity2Api::class, declaredOnly = false, setup = SetupType.Annotated)))
        Assert.assertTrue( apis.get("app"   , "tests", "patch" ).success)
        Assert.assertTrue( apis.get("app"   , "tests", "recent" ).success)
        Assert.assertTrue( apis.get("app"   , "tests", "deleteById" ).success)
    }


    @Test fun can_check_action_does_NOT_exist() {
        val apis = ApiServer(ctx, apis = listOf(
                Api(SamplePOKOApi::class, "app", "SamplePOKO"),
                Api(InfoApi(ctx), setup = SetupType.Annotated),
                Api(VersionApi(ctx), setup = SetupType.Annotated)
        ))

        Assert.assertTrue(!apis.routes.contains("app.SamplePOKO.fakeMethod"))
        Assert.assertTrue(!apis.routes.contains("sys.app.host2"))
    }


    @Test fun can_check_action_exists() {
        val apis = ApiServer(ctx, apis = listOf(
                Api(SamplePOKOApi::class, "app", "SamplePOKO"),
                Api(InfoApi(ctx), setup = SetupType.Annotated),
                Api(VersionApi(ctx), setup = SetupType.Annotated)
        ))

        Assert.assertTrue(apis.routes.check("app.SamplePOKO.getCounter"))
        Assert.assertTrue(apis.routes.check("app.info.about"))
    }


    @Test fun can_call_action_without_area() {
        val apis = ApiServer(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")))
        val result = runBlocking {  apis.executeAttempt("app", "SamplePOKO", "getCounter", Verb.Auto, mapOf(), mapOf()) }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } == 1)
    }


    @Test fun can_call_action_with_request() {
        val apis = ApiServer(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")))
        val result = runBlocking {
            apis.executeAttempt("app", "SamplePOKO", SamplePOKOApi::request.name, Verb.Auto, mapOf(), mapOf("greeting" to "hi"))
        }
        Assert.assertTrue(result.success)
    }


    @Test fun can_call_action_in_derived_class() {
        val apis = ApiServer(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended")))
        val result = runBlocking { apis.executeAttempt("app", "SampleExtended", "getSeconds", Verb.Auto, mapOf(), mapOf()) }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { -1 } in 0..59)
    }


    @Test fun can_call_action_in_base_class() {
        val apis = ApiServer(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended", declaredOnly = false)))
        val result = runBlocking { apis.executeAttempt("app", "SampleExtended", "getCounter", Verb.Auto, mapOf(), mapOf()) }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } == 1)
    }


    @Test
    fun can_get_api_info_from_method() {
        val ctx = AppContext.simple(TestApp::class.java,"queues")
        val api = WorkerSampleApi(ctx)
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = SetupType.Annotated)) )
        val apiRef = apis.get(WorkerSampleApi::class, WorkerSampleApi::test1)
        Assert.assertTrue( apiRef.getOrElse { null }?.api?.area == "samples")
        Assert.assertTrue( apiRef.getOrElse { null }?.api?.name == "workerqueue")
        Assert.assertTrue( apiRef.getOrElse { null }?.action?.name == "test1")
    }
}
