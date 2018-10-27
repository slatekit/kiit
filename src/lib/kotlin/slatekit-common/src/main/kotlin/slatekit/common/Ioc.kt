package slatekit.common

object Ioc {
    val lookup = mutableMapOf<String, (String) -> Any>()

    fun register(name: String, creator: (String) -> Any) {
        lookup.put(name, creator)
    }

    fun <T> get(name: String): T {
        if (!lookup.containsKey(name)) {
            throw IllegalArgumentException("Component $name not in Ioc")
        }
        val creator = lookup.get(name)!!
        val instance = creator.invoke(name) as T
        return instance
    }

    fun contains(name: String): Boolean {
       return lookup.containsKey(name)
    }
}