package slatekit.generator

import slatekit.common.ext.toId
import java.io.File

/**
 * @param name        : Name of app e.g. "MyApp1"
 * @param packageName : Name of package e.g. "MyCompany.product1"
 */
data class GeneratorContext(val rootDir: File,
                            val destDir: File,
                            val name: String,
                            val desc: String,
                            val packageName: String,
                            val company: String,
                            val area: String,
                            val mode: CredentialMode,
                            val settings: GeneratorSettings)
{
    /**
     * Normalizes the fields to ensure proper names are created.
     * E.g. Removes spaces from package Name
     */
    fun normalize(settings: GeneratorSettings): GeneratorContext {
        val canonicalName = name
        val canonicalPackage = packageName.toId()
        return GeneratorContext(rootDir, destDir, canonicalName, desc, canonicalPackage, company, area, mode, settings)
    }
}
