package slatekit.providers.metrics

import io.micrometer.core.instrument.ImmutableTag
import slatekit.common.envs.Envs
import slatekit.core.common.AppContext


/**
 * Standardized set of tags used for metrics.
 * This tags are associated w/ the common SlateKit context values (env, app, etc )
 */
class Tags(val ctx: AppContext) {

    /**`
     * Environment e.g. qa1.qa
     */
    val env: Tag = Tag("env", ctx.env.key)


    /**
     * Application name: user-service
     */
    val app: Tag = Tag("app", ctx.app.about.name)


    /**
     * Group or department: Registration
     */
    val grp: Tag = Tag("grp", ctx.app.about.group)


    /**
     * Host name
     */
    val host: Tag = Tag("host", ctx.app.host.name)


    /**
     * Location: Relevant location/region
     */
    val loc: Tag = Tag("loc", ctx.app.about.region)


    /**
     * List of all the global tags above
     */
    val global:List<Tag> = listOf(env, app, grp, host, loc)


    private val paths = mutableMapOf<String,Tag>()
    /**
     * A uri of a resource
     */
    fun uri(path:String): Tag {
        return if(paths.containsKey(path)) {
            paths[path]!!
        } else {
            val tag = Tag("uri", path)
            paths.put(path, tag )
            tag
        }
    }
}