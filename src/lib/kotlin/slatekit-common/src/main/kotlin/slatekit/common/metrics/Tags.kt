package slatekit.common.metrics


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
            val tag = Tagged("uri", path)
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
                Tagged("env", envName),
                Tagged("app", appName),
                Tagged("grp", groupName),
                Tagged("host", hostName),
                Tagged("loc", location),
                Tagged("shard", shardName)
        )
    }
}