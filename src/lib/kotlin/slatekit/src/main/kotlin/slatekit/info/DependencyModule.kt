package slatekit.info

import slatekit.apis.core.Api
import slatekit.apis.ApiModule
import slatekit.integration.common.AppEntContext
import slatekit.integration.mods.Module
import slatekit.integration.mods.ModuleContext
import slatekit.integration.mods.ModuleInfo

class DependencyModule(
        val appEntCtx: AppEntContext,
        modCtx: ModuleContext
) : Module(appEntCtx.toAppContext(), modCtx), ApiModule {

    override val info = ModuleInfo(
            name = "slatekit.info.dependency",
            desc = "Sample Entity",
            version = "1.0",
            isInstalled = false,
            isEnabled = true,
            isDbDependent = true,
            totalModels = 1,
            source = Dependency::class.qualifiedName ?: "",
            dependencies = "none",
            models = listOf(
                    Dependency::class.qualifiedName ?: ""
            )
    )


    /**
     * hook to initialize
     */
    override fun init() {

    }


    /**
     * Handles registration of the module.
     * In this case, registers the Dependency entity/service with the ORM
     */
    override fun register() {

    }


    /**
     * Gets all the apis associated with this module
     */
    override fun getApis(): List<Api> {
        return listOf()
        //return listOf( (Api(DependencyApi(appEntCtx), declaredOnly = false, setup = Annotated)))
    }


    /**
     * Used to seed any initial data
     */
    override fun seed() {
        val dependencyService = appEntCtx.ent.getSvc<Long, Dependency>(Dependency::class) as DependencyService
        dependencyService.seed()
    }
}