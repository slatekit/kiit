package kiit

import kiit.apis.setup.ApiSetup
import kiit.apis.setup.api
import kiit.apis.tools.code.CodeGenApi
import kiit.common.conf.Conf
import kiit.context.Context
import kiit.docs.DocApi
import kiit.generator.*

interface KiitServices {

    val ctx: Context

    fun apis(settings:Conf): List<ApiSetup> {
        // APIs
        val toolSettings = ToolSettings(settings.getString("kiit.version"), settings.getString("kiit.version.beta"), "logs/logback.log")
        val buildSettings = BuildSettings(settings.getString("kotlin.version"))
        val logger = ctx.logs.getLogger("gen")
        val generator = GeneratorApi(ctx, GeneratorService(ctx, settings, Kiit::class.java, GeneratorSettings(toolSettings, buildSettings), logger = logger))
        return listOf(
                api(GeneratorApi::class, generator),
                api(DocApi::class, DocApi(ctx)),
                api(CodeGenApi::class, CodeGenApi())
            )
    }
}