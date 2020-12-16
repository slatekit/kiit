package slatekit.apis.support

import slatekit.apis.Action
import slatekit.apis.Api
import slatekit.apis.core.Target
import slatekit.apis.routes.Routes
import slatekit.common.requests.Request
import slatekit.context.Context
import slatekit.meta.Reflector
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

interface RouteSupport {
    val ctx: Context
    val routes:Routes


    /**
     * gets the api info associated with the request
     * @param cmd
     * @return
     */
    fun get(req: Request): Outcome<Target> {
        return get(req.area, req.name, req.action)
    }


    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun get(area: String, name: String, action: String): Outcome<Target> {
        return routes.api(area, name, action, ctx)
    }

    /**
     * gets the mapped method associated with the api action.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun get(clsType: KClass<*>, member: KCallable<*>): Outcome<Target> {
        val apiAnno = Reflector.getAnnotationForClassOpt<Api>(clsType, Api::class)
        val result = apiAnno?.let { anno ->

            val area = anno.area
            val api = anno.name
            val actionAnno = Reflector.getAnnotationForMember<Action>(member, Action::class)
            val action = actionAnno?.let { act ->
                val action = if (act.name.isBlank()) member.name else act.name
                action
            } ?: member.name
            val info = get(area, api, action)
            info
        } ?: Outcomes.errored("member/annotation not found for : ${member.name}")
        return result
    }
}
