package slatekit.apis.routes

open class Lookup<T>(
    val items: List<T>,
    val nameFetcher: (T) -> String
) {

    val size = items.size
    val keys = items.map(this.nameFetcher)
    val map = items.map { it -> Pair(this.nameFetcher(it), it) }.toMap()

    fun contains(name: String): Boolean = map.contains(name)
    operator fun get(name: String): T? = if (contains(name)) map[name] else null
}
