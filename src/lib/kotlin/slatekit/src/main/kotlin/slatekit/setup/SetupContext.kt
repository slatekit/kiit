package slatekit.setup

/**
 * @param name        :
 * @param packageName :
 */
data class SetupContext(val name: String,
                        val desc: String,
                        val packageName: String,
                        var destination: String)
