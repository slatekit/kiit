package test.apis

import org.junit.Test
import slatekit.apis.ApiContainer
import slatekit.apis.ApiRef
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.common.getOrElse
import slatekit.core.common.AppContext
import slatekit.integration.apis.AppApi
import slatekit.integration.apis.VersionApi
import test.setup.*


class Api_Setup_Tests : ApiTestsBase() {


    @Test fun can_setup_instance_as_new() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")), allowIO = false)
        val result = apis.getApi("app", "SamplePOKO", "getTime" )

        val apiRef = result.getOrElse { null }
        assert(result.success && apiRef?.instance is SamplePOKOApi)
        assert((apiRef?.instance as SamplePOKOApi).count == 0)
    }


    @Test fun can_setup_instance_as_new_with_context() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleEntityApi::class, "app", "SampleEntity")), allowIO = false)
        val result = apis.getApi("app", "SampleEntity", "patch" )

        val apiRef = result.getOrElse { null }
        assert(result.success && apiRef?.instance is SampleEntityApi)
    }


    @Test fun can_setup_instance_as_singleton() {
        val inst = SamplePOKOApi()
        inst.count = 1001
        val apis = ApiContainer(ctx, apis = listOf(Api(inst, "app", "SamplePOKO")), allowIO = false)
        val result = apis.getApi("app", "SamplePOKO", "getTime" )
        val apiRef = result.getOrElse { null }
        assert(result.success && apiRef?.instance is SamplePOKOApi)
        assert((apiRef?.instance as SamplePOKOApi).count == 1001)
    }


    @Test fun can_setup_instance_with_declared_members_only() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")), auth = null, allowIO = false)
        assert( apis.getApi("app"   , "SamplePOKO", "getTime"    ).success)
        assert( apis.getApi("app"   , "SamplePOKO", "getCounter" ).success)
        assert( apis.getApi("app"   , "SamplePOKO", "hello"      ).success)
        assert( apis.getApi("app"   , "SamplePOKO", "request"    ).success)
        assert( apis.getApi("app"   , "SamplePOKO", "response"   ).success)
        assert(!apis.getApi("app"   , "SamplePOKO", "getEmail"   ).success)
        assert(!apis.getApi("app"   , "SamplePOKO", "getSsn"     ).success)
    }


    @Test fun can_setup_instance_with_inheritance() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended", declaredOnly = false)), auth = null, allowIO = false)
        assert( apis.getApi("app"   , "SampleExtended", "getSeconds" ).success)
        assert( apis.getApi("app"   , "SampleExtended", "getTime"    ).success)
        assert( apis.getApi("app"   , "SampleExtended", "getCounter" ).success)
        assert( apis.getApi("app"   , "SampleExtended", "hello"      ).success)
        assert( apis.getApi("app"   , "SampleExtended", "request"    ).success)
        assert( apis.getApi("app"   , "SampleExtended", "response"   ).success)
        assert(!apis.getApi("app"   , "SampleExtended", "getEmail"   ).success)
        assert(!apis.getApi("app"   , "SampleExtended", "getSsn"     ).success)
    }


    @Test fun can_setup_instance_with_compositional_apis_with_annotations() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleEntity2Api::class, declaredOnly = false, setup = Annotated)),
                auth = null, allowIO = false)
        assert( apis.getApi("app"   , "tests", "patch" ).success)
        assert( apis.getApi("app"   , "tests", "recent" ).success)
        assert( apis.getApi("app"   , "tests", "deleteById" ).success)
    }


    @Test fun can_check_action_does_NOT_exist() {
        val apis = ApiContainer(ctx, apis = listOf(
                Api(SamplePOKOApi::class, "app", "SamplePOKO"),
                Api(AppApi(ctx), setup = Annotated),
                Api(VersionApi(ctx), setup = Annotated)
        ), auth = null, allowIO = false)

        assert(!apis.contains("app.SamplePOKO.fakeMethod"))
        assert(!apis.contains("sys.app.host2"))
    }


    @Test fun can_check_action_exists() {
        val apis = ApiContainer(ctx, apis = listOf(
                Api(SamplePOKOApi::class, "app", "SamplePOKO"),
                Api(AppApi(ctx), setup = Annotated),
                Api(VersionApi(ctx), setup = Annotated)
        ), auth = null, allowIO = false)

        assert(apis.contains("app.SamplePOKO.getCounter"))
        assert(apis.contains("sys.app.host"))
    }


    @Test fun can_call_action_without_area() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SamplePOKOApi::class, "app", "SamplePOKO")), auth = null, allowIO = false)
        val result = apis.call("app", "SamplePOKO", "getCounter", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.getOrElse { 0 } == 1)
    }


    @Test fun can_call_action_in_derived_class() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended")), auth = null, allowIO = false)
        val result = apis.call("app", "SampleExtended", "getSeconds", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.getOrElse { -1 } in 0..59)
    }


    @Test fun can_call_action_in_base_class() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleExtendedApi::class, "app", "SampleExtended", declaredOnly = false)), auth = null, allowIO = false)
        val result = apis.call("app", "SampleExtended", "getCounter", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.getOrElse { 0 } == 1)
    }


    @Test
    fun can_get_api_info_from_method(){
        val ctx = AppContext.simple("queues")
        val api = WorkerSampleApi(ctx)
        val apis = ApiContainer(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null , allowIO = false)
        val apiRef = apis.getApi(WorkerSampleApi::class, WorkerSampleApi::test1)
        assert( apiRef.getOrElse { null }?.api?.area == "samples")
        assert( apiRef.getOrElse { null }?.api?.name == "workerqueue")
        assert( apiRef.getOrElse { null }?.action?.name == "test1")
    }
}
