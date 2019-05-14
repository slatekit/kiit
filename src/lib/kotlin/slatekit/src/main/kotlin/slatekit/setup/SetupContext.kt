package slatekit.setup

import slatekit.common.toId

/**
 * @param name        :
 * @param packageName :
 */
data class SetupContext(val name: String,
                        val desc: String,
                        val packageName: String,
                        val company: String,
                        var destination: String)
{
    fun normalize():SetupContext {
        val canonicalName = name
        val canonicalPackage = packageName.toId()
        return SetupContext(canonicalName, desc, canonicalPackage, company, destination)
    }
}
