package kiit.apis.routes


interface Lookup<T> {
    val parent:String
    val items:List<T>
    val map:Map<String, T>
    val size:Int get() { return map.size }
    fun contains(key:String):Boolean = map.containsKey(key)
    fun get(key:String):T? = map[key]
}

/**
 * Look up for all actions on an API
 */
class ActionLookup(val api:Api, override val items: List<RouteMapping>) : Lookup<RouteMapping> {
    override val parent: String = "${api.version}:${api.name}"
    override val map: Map<String, RouteMapping> = toMap(items)

    companion object {
        fun toMap(mappings: List<RouteMapping>): Map<String, RouteMapping> {
            val pairs = mappings.map {
                // key = "{VERB}.{VERSION}.{NAME}"
                val action = it.route.action
                val name = "${action.version}.${action.version}:${action.name}"
                name to it
            }
            return pairs.toMap()
        }
    }
}


/**
 * Lookup for all apis on an Area
 */
class ApiLookup(val area:Area, override val items: List<ActionLookup>) : Lookup<ActionLookup> {
    override val parent: String = area.name
    override val map: Map<String, ActionLookup> = items.map { Pair("${it.api.version}:${it.api.name}", it) }.toMap()
}


/**
 * Lookup for all areas in the routes
 */
class AreaLookup(val area:Area, override val items: List<ApiLookup>) : Lookup<ApiLookup> {
    override val parent: String = ""
    override val map: Map<String, ApiLookup> = items.map { Pair(it.parent, it) }.toMap()
}
