package kiit.apis.routes

/**
 * General interface for the Route Tree ( only 2-3 levels deep )
 */
interface Lookup<T> {
    val name:String
    val items:List<T>
    val map:Map<String, T>
    val size:Int get() { return map.size }
    fun contains(key:String):Boolean = map.containsKey(key)
    fun get(key:String):T? = map[key]
}


/**
 * Lookup for all apis on an Area
 */
data class Routes(override val items: List<AreaApis>) : Lookup<AreaApis> {
    override val name: String = "root"
    override val map: Map<String, AreaApis> = items.associateBy { it.name }
}


/**
 * Lookup for all apis on an Area
 */
data class AreaApis(val area:Area, override val items: List<ApiActions>) : Lookup<ApiActions> {
    override val name: String = area.name
    override val map: Map<String, ApiActions> = items.associateBy { it.name }
}


/**
 * Look up for all actions on an API
 */
data class ApiActions(val api:Api, override val items: List<Route>) : Lookup<Route> {
    override val name: String = "${api.version}:${api.name}"
    override val map: Map<String, Route> = toMap(items)

    companion object {
        fun toMap(mappings: List<Route>): Map<String, Route> {
            val pairs = mappings.map {
                // key = "{VERB}.{VERSION}.{NAME}"
                val action = it.action
                val name = "${action.verb.name}.${action.version}:${action.name}"
                name to it
            }
            return pairs.toMap()
        }
    }
}
