package kiit.telemetry

/**
 * Represents a single tag ( name/value pair ) to associate metrics with
 */
interface Tag {
    val tagName:String
    val tagVal:String
}



interface Tagged {
    val tags: List<Tag>
}



class SimpleTag(override val tagName:String, override val tagVal:String): Tag



/**
 * Standardized set of tags used for metrics.
 * This tags are associated w/ the common SlateKit context values (env, app, etc )
 */
class Tags(val global: List<Tag>) {

    /**
     * @param envName  : Environment e.g. qa1.qa
     * @param appName  : Application name: user-service
     * @param groupName: Group or department: Registration
     * @param hostName : Host name
     * @param location : Location: Relevant location/region
     * @param shardName: Shard name in a partitioned system
     *
     */
    constructor(envName: String,
                appName: String,
                groupName: String,
                hostName: String,
                location: String,
                shardName: String) :
            this(build(envName, appName, groupName, hostName, location, shardName))


    private val paths = mutableMapOf<String, Tag>()


    /**
     * A uri of a resource
     */
    fun uri(path: String): Tag {
        return if (paths.containsKey(path)) {
            paths[path]!!
        } else {
            val tag = SimpleTag("uri", path)
            paths[path] = tag
            tag
        }
    }

    companion object {
        fun build(envName: String,
                  appName: String,
                  groupName: String,
                  hostName: String,
                  location: String,
                  shardName: String): List<Tag> = listOf(
                SimpleTag("env", envName),
                SimpleTag("app", appName),
                SimpleTag("grp", groupName),
                SimpleTag("host", hostName),
                SimpleTag("loc", location),
                SimpleTag("shard", shardName)
        )
    }
}
