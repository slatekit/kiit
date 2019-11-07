package slatekit.generator

import slatekit.common.toId
import java.io.File

/**
 * @param name        :
 * @param packageName :
 */
data class GeneratorContext(val rootDir: File,
                            val name: String,
                            val desc: String,
                            val packageName: String,
                            val company: String,
                            val destination: String,
                            val mode: CredentialMode)
{
    /**
     * Normalizes the fields to ensure proper names are created.
     * E.g. Removes spaces from package Name
     */
    fun normalize(): GeneratorContext {
        val canonicalName = name
        val canonicalPackage = packageName.toId()
        return GeneratorContext(rootDir, canonicalName, desc, canonicalPackage, company, destination, mode)
    }
}
