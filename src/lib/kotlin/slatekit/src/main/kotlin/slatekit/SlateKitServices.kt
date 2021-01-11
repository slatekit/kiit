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
        val toolSettings = ToolSettings(settings.getString("slatekit.version"), settings.getString("slatekit.version.beta"))
        val buildSettings = BuildSettings(settings.getString("kotlin.version"))
        return listOf(
                Api(GeneratorApi(ctx, GeneratorService(ctx, settings, SlateKit::class.java, GeneratorSettings(toolSettings, buildSettings))), declaredOnly = true, setup = SetupType.Annotated),
                Api(DocApi(ctx), declaredOnly = true, setup = SetupType.Annotated),
                Api(CodeGenApi(), declaredOnly = true, setup = SetupType.Annotated)
        )
    }
}