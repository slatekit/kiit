package slatekit

import slatekit.apis.SetupType
import slatekit.apis.routes.Api
import slatekit.apis.tools.code.CodeGenApi
import slatekit.context.Context
import slatekit.docs.DocApi
import slatekit.generator.*
//import slatekit.samples.common.apis.SampleApi

interface SlateKitServices {

    val ctx: Context

    fun apis(): List<Api> {
//        // Module api
//        val moduleApi = ModuleApi(moduleContext(), ctx)
//        moduleApi.register(DependencyModule(ctx, moduleContext()))

        // APIs
        val toolSettings = ToolSettings(
                this.ctx.conf.getString("slatekit.version"),
                this.ctx.conf.getString("slatekit.version.beta"))
        val buildSettings = BuildSettings(this.ctx.conf.getString("kotlin.version"))
        val requiredApis = listOf(
                Api(GeneratorApi(ctx, GeneratorService(ctx, SlateKit::class.java, GeneratorSettings(toolSettings, buildSettings))), declaredOnly = true, setup = SetupType.Annotated),
                Api(DocApi(ctx), declaredOnly = true, setup = SetupType.Annotated),
                Api(CodeGenApi(), declaredOnly = true, setup = SetupType.Annotated)
                //Api(SampleApi(ctx), declaredOnly = true, setup = SetupType.Annotated)
//                Api(InfoApi(ctx), declaredOnly = true, setup = Setup.Annotated),
//                Api(VersionApi(ctx), declaredOnly = true, setup = Setup.Annotated)
                //Api(moduleApi, declaredOnly = true, setup = SetupType.Annotated)
        )
        val optionalApis = listOf<Api>() //optionalApis()
        val allApis = requiredApis.plus(optionalApis)
        return allApis
    }


    fun optionalApis(): List<Api> {
        // @param key : "email"
        fun load(key: String, call: () -> Api): Api? {
            val enabled = ctx.conf.getBoolOrElse(key, false)
            return if (enabled) call() else null
        }

        val apis = listOf(
                //load("email") { Api(EmailApi(Builder.emails(ctx), ctx), declaredOnly = true, setup = SetupType.Annotated) },
//                load("files") { Api(FilesApi(Builder.files(ctx), ctx), declaredOnly = true, setup = SetupType.Annotated) },
//                load("queues") { Api(QueueApi(Builder.queues(ctx), ctx), declaredOnly = true, setup = SetupType.Annotated) },
                //load("sms") { Api(SmsApi(Builder.sms(ctx), ctx), declaredOnly = true, setup = SetupType.Annotated) },
                Api(CodeGenApi(), declaredOnly = true, setup = SetupType.Annotated)
                //load("db") { Api(DependencyApi(ctx), declaredOnly = false, setup = Setup.Annotated) }
        )
        return apis.filterNotNull()
    }
}