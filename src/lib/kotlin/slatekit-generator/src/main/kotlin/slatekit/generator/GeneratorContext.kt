package slatekit.generator

import slatekit.common.ext.toId
import java.io.File

/**
 * @param name        :
 * @param packageName :
 */
data class GeneratorContext(val rootDir: File,
                            val destDir: File,
                            val name: String,
                            val desc: String,
                            val packageName: String,
                            val company: String,
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
        return GeneratorContext(rootDir, destDir, canonicalName, desc, canonicalPackage, company, mode, settings)
    }
}
