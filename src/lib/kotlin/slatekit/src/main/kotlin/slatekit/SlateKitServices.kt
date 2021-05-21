package slatekit

import slatekit.apis.SetupType
import slatekit.apis.routes.Api
import slatekit.apis.tools.code.CodeGenApi
import slatekit.common.conf.Conf
import slatekit.context.Context
import slatekit.docs.DocApi
import slatekit.generator.*

interface SlateKitServices {

    val ctx: Context

    fun apis(settings:Conf): List<Api> {
        // APIs
        val toolSettings = ToolSettings(settings.getString("slatekit.version"), settings.getString("slatekit.version.beta"), "logs/logback.log")
        val buildSettings = BuildSettings(settings.getString("kotlin.version"))
        val logger = ctx.logs.getLogger("gen")
        return listOf(
                Api(GeneratorApi(ctx, GeneratorService(ctx, settings, SlateKit::class.java, GeneratorSettings(toolSettings, buildSettings), logger = logger)), declaredOnly = true, setup = SetupType.Annotated),
                Api(DocApi(ctx), declaredOnly = true, setup = SetupType.Annotated),
                Api(CodeGenApi(), declaredOnly = true, setup = SetupType.Annotated)
        )
    }
}