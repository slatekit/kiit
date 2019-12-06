package slatekit.generator

import slatekit.common.ext.toId
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
                            val mode: CredentialMode,
                            val tool: ToolSettings)
{
    /**
     * Normalizes the fields to ensure proper names are created.
     * E.g. Removes spaces from package Name
     */
    fun normalize(tool: ToolSettings): GeneratorContext {
        val canonicalName = name
        val canonicalPackage = packageName.toId()
        return GeneratorContext(rootDir, canonicalName, desc, canonicalPackage, company, destination, mode, tool)
    }
}
