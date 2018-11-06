package slatekit.common.metrics


/**
 * Standardized set of tags used for metrics.
 * This tags are associated w/ the common SlateKit context values (env, app, etc )
 */
class Tags(envName: String,
           appName: String,
           groupName: String,
           hostName: String,
           location: String,
           shardName: String) {

    /**`
     * Environment e.g. qa1.qa
     */
    val env: Tag = MetricTag("env", envName)


    /**
     * Application name: user-service
     */
    val app: Tag = MetricTag("app", appName)


    /**
     * Group or department: Registration
     */
    val grp: Tag = MetricTag("grp", groupName)


    /**
     * Host name
     */
    val host: Tag = MetricTag("host", hostName)


    /**
     * Location: Relevant location/region
     */
    val loc: Tag = MetricTag("loc", location)


    /**
     * Shard name in a partitioned system
     */
    val shard: Tag = MetricTag("shard", shardName)


    /**
     * List of all the global tags above
     */
    val global: List<Tag> = listOf(env, app, grp, host, loc)


    private val paths = mutableMapOf<String, Tag>()
    /**
     * A uri of a resource
     */
    fun uri(path: String): Tag {
        return if (paths.containsKey(path)) {
            paths[path]!!
        } else {
            val tag = MetricTag("uri", path)
            paths.put(path, tag)
            tag
        }
    }
}