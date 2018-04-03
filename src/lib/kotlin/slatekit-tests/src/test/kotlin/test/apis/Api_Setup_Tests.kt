package test.apis

import org.junit.Test
import slatekit.apis.ApiContainer
import slatekit.apis.core.Api
import slatekit.core.common.AppContext
import slatekit.integration.apis.AppApi
import slatekit.integration.apis.VersionApi
import test.setup.SampleEntityApi
import test.setup.SampleExtendedApi
import test.setup.SamplePOKOApi
import test.setup.WorkerSampleApi


class Api_Setup_Tests : ApiTestsBase() {


    @Test fun can_setup_instance_as_new() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SamplePOKOApi::class)), allowIO = false)
        val result = apis.getApi("", "SamplePOKO", "getTime" )
        assert(result.success && result.value!!.instance is SamplePOKOApi)
        assert((result.value!!.instance as SamplePOKOApi).count == 0)
    }


    @Test fun can_setup_instance_as_new_with_context() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleEntityApi::class)), allowIO = false)
        val result = apis.getApi("", "SampleEntity", "patch" )
        assert(result.success && result.value!!.instance is SampleEntityApi)
    }


    @Test fun can_setup_instance_as_singleton() {
        val inst = SamplePOKOApi()
        inst.count = 1001
        val apis = ApiContainer(ctx, apis = listOf(Api(inst)), allowIO = false)
        val result = apis.getApi("", "SamplePOKO", "getTime" )
        assert(result.success && result.value!!.instance is SamplePOKOApi)
        assert((result.value!!.instance as SamplePOKOApi).count == 1001)
    }


    @Test fun can_setup_instance_with_declared_members_only() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SamplePOKOApi::class)), auth = null, allowIO = false)
        assert( apis.getApi(""   , "SamplePOKO", "getTime"    ).success)
        assert( apis.getApi(""   , "SamplePOKO", "getCounter" ).success)
        assert( apis.getApi(""   , "SamplePOKO", "hello"      ).success)
        assert( apis.getApi(""   , "SamplePOKO", "request"    ).success)
        assert( apis.getApi(""   , "SamplePOKO", "response"   ).success)
        assert(!apis.getApi(""   , "SamplePOKO", "getEmail"   ).success)
        assert(!apis.getApi(""   , "SamplePOKO", "getSsn"     ).success)
    }


    @Test fun can_setup_instance_with_inheritance() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleExtendedApi::class, declaredOnly = false)), auth = null, allowIO = false)
        assert( apis.getApi(""   , "SampleExtended", "getSeconds" ).success)
        assert( apis.getApi(""   , "SampleExtended", "getTime"    ).success)
        assert( apis.getApi(""   , "SampleExtended", "getCounter" ).success)
        assert( apis.getApi(""   , "SampleExtended", "hello"      ).success)
        assert( apis.getApi(""   , "SampleExtended", "request"    ).success)
        assert( apis.getApi(""   , "SampleExtended", "response"   ).success)
        assert(!apis.getApi(""   , "SampleExtended", "getEmail"   ).success)
        assert(!apis.getApi(""   , "SampleExtended", "getSsn"     ).success)
    }


    @Test fun can_register_after_initial_setup() {
        val apis = ApiContainer(ctx, apis = listOf(
                Api(SamplePOKOApi::class),
                Api(AppApi(ctx)),
                Api(VersionApi(ctx))
        ), auth = null, allowIO = false)

        assert(apis.getApi(""   , "SamplePOKO", "getTime"  ).success)
        assert(apis.getApi("sys", "app"       , "host"     ).success)
        assert(apis.getApi("sys", "version"   , "java"     ).success)
    }


    @Test fun can_check_action_does_NOT_exist() {
        val apis = ApiContainer(ctx, apis = listOf(
                Api(SamplePOKOApi::class),
                Api(AppApi(ctx)),
                Api(VersionApi(ctx))
        ), auth = null, allowIO = false)

        assert(!apis.contains("SamplePOKO.fakeMethod"))
        assert(!apis.contains("sys.app.host2"))
    }


    @Test fun can_check_action_exists() {
        val apis = ApiContainer(ctx, apis = listOf(
                Api(SamplePOKOApi::class),
                Api(AppApi(ctx)),
                Api(VersionApi(ctx))
        ), auth = null, allowIO = false)

        assert(apis.contains("SamplePOKO.getCounter"))
        assert(apis.contains("sys.app.host"))
    }


    @Test fun can_call_action_without_area() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SamplePOKOApi::class)), auth = null, allowIO = false)
        val result = apis.call("", "SamplePOKO", "getCounter", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.value == 1)
    }


    @Test fun can_call_action_in_derived_class() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleExtendedApi::class)), auth = null, allowIO = false)
        val result = apis.call("", "SampleExtended", "getSeconds", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.value in 0..59)
    }


    @Test fun can_call_action_in_base_class() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleExtendedApi::class, declaredOnly = false)), auth = null, allowIO = false)
        val result = apis.call("", "SampleExtended", "getCounter", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.value == 1)
    }


    @Test
    fun can_get_api_info_from_method(){
        val ctx = AppContext.simple("queues")
        val api = WorkerSampleApi(ctx)
        val apis = ApiContainer(ctx, apis = listOf(Api(api)), auth = null , allowIO = false)
        val apiRef = apis.getApi(WorkerSampleApi::class, WorkerSampleApi::test1)
        assert( apiRef.value?.api?.area == "samples")
        assert( apiRef.value?.api?.name == "workerqueue")
        assert( apiRef.value?.action?.name == "test1")
    }
}
