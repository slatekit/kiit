package kiit.apis.routes


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
class Areas(override val items: List<Apis>) : Lookup<Apis> {
    override val name: String = "root"
    override val map: Map<String, Apis> = items.associateBy { it.name }
}


/**
 * Lookup for all apis on an Area
 */
class Apis(val area:Area, override val items: List<Actions>) : Lookup<Actions> {
    override val name: String = area.name
    override val map: Map<String, Actions> = items.associateBy { it.name }
}


/**
 * Look up for all actions on an API
 */
class Actions(val api:Api, override val items: List<Route>) : Lookup<Route> {
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


/**
 * Lookup for all areas in a global version
 */
class VersionAreas(val version:String, override val items: List<Apis>) : Lookup<Apis> {
    override val name: String = version
    override val map: Map<String, Apis> = items.associateBy { it.name }
}
