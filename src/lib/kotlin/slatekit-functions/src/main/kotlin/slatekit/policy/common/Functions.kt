package slatekit.policy.common

/**
 * Command manager to run commands and get back the status of each command
 * and their last results.
 */
interface Functions<out T : Function> {

    /**
     * All the functions
     */
    val all: List<T>

    /**
     * names of the functions
     */
    val names: List<String> get() { return all.map { f -> f.id.name } }

    /**
     * number of functions
     */
    val size: Int get() { return all.size }

    /**
     * whether or not there is a function with the supplied name.
     * @param name
     * @return
     */
    fun contains(name: String): Boolean = all.count { it.id.name == name } > 0

    /**
     * Get the first function with a matching name
     */
    fun getOrNull(name: String): T? {
        return all.firstOrNull { it.id.name == name }
    }
}
