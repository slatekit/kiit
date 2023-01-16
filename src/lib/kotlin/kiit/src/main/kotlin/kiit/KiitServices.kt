package kiit

import kiit.apis.SetupType
import kiit.apis.routes.Api
import kiit.apis.tools.code.CodeGenApi
import kiit.common.conf.Conf
import kiit.context.Context
import kiit.docs.DocApi
import slatekit.generator.*

interface KiitServices {

    val ctx: Context

    fun apis(settings:Conf): List<Api> {
        // APIs
        val toolSettings = ToolSettings(settings.getString("kiit.version"), settings.getString("kiit.version.beta"), "logs/logback.log")
        val buildSettings = BuildSettings(settings.getString("kotlin.version"))
        val logger = ctx.logs.getLogger("gen")
        return listOf(
                Api(GeneratorApi(ctx, GeneratorService(ctx, settings, Kiit::class.java, GeneratorSettings(toolSettings, buildSettings), logger = logger)), declaredOnly = true, setup = SetupType.Annotated),
                Api(DocApi(ctx), declaredOnly = true, setup = SetupType.Annotated),
                Api(CodeGenApi(), declaredOnly = true, setup = SetupType.Annotated)
        )
    }
}