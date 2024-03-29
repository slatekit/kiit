/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.apis.services

import kiit.apis.ApiServer
import kiit.apis.core.Part
import kiit.apis.routes.Router
import kiit.apis.tools.docs.Doc
import kiit.apis.tools.docs.DocUtils
import kiit.common.types.Content
import kiit.common.types.Contents
import kiit.requests.Request
import kiit.results.*
import kiit.results.builders.Outcomes


open class Help(val host: ApiServer, val routes: Router, val docKey: String?, val docBuilder: () -> Doc) {

    fun process(req: Request): Outcome<Content> {
        val result = DocUtils.isHelp(req)
        return when (result) {
            is Success -> build(req, result.value)
            is Failure -> result
        }
    }

    /**
     * Handles help request on any part of the api request. Api requests are typically in
     * the format "area.api.action" so you can type help on each part / region.
     * e.g.
     * 1. area ?
     * 2. area.api ?
     * 3. area.api.action ?
     */
    fun build(req: Request, helpType: Part): Outcome<Content> {
        return if (!DocUtils.hasDocKey(req, docKey ?: "")) {
            Outcomes.denied("Unauthorized access to API docs")
        } else {
            val content = when (helpType) {
                // 1: ? = help on all
                Part.All -> {
                    areas()
                }
                // 2: {area} ? = help on area
                Part.Area -> {
                    area(req.parts[0])
                }
                // 3. {area}.{api} = help on api
                Part.Api -> {
                    api(req.parts[0], req.parts[1])
                }
                // 4. {area}.{api}.{action} = help on api
                Part.Action -> {
                    action(req.parts[0], req.parts[1], req.parts[2])
                }
            }
            Outcomes.success(Contents.html(content))
        }
    }

    /**
     * handles help request for all the areas supported
     *
     * @return
     */
    open fun areas(): String {
        val doc = docBuilder()
        //doc.areas(routes.areas)
        return "" //doc.toString()
    }

    /**
     * handles help request for a specific area
     *
     * @param area
     * @return
     */
    open fun area(name: String): String {
//        val doc = docBuilder()
//        if (!routes.contains(name)) {
//            doc.error("Area : $name not found")
//        } else {
//            val area = routes.areas[name]
//            area?.let { doc.area(it) }
//        }
//        return doc.toString()
        return ""
    }

    /**
     * handles help request for a specific api
     *
     * @param area
     * @param apiName
     * @return
     */
    open fun api(areaName: String, apiName: String): String {
//        val doc = docBuilder()
//        if (!routes.contains(areaName, apiName)) {
//            doc.error("Area : $areaName not found")
//        } else {
//            val area = routes.areas[areaName]
//            area?.let { area ->
//                val api = area.apis[apiName]
//                api?.let { api ->
//                    doc.api(area, api)
//                }
//            }
//        }
//        return doc.toString()
        return ""
    }

    /**
     * handles help request for a specific api action
     *
     * @param area
     * @param apiName
     * @param actionName
     * @return
     */
    open fun action(areaName: String, apiName: String, actionName: String): String {
//        val doc = docBuilder()
//        if (!routes.contains(areaName, apiName)) {
//            doc.error("Area : $areaName not found")
//        } else {
//            val area = routes.areas[areaName]
//            area?.let { area ->
//                val api = area.apis[apiName]
//                api?.let { api ->
//                    val action = api.actions[actionName]
//                    action?.let { action ->
//                        doc.action(area, api, action)
//                    }
//                }
//            }
//        }
//        return doc.toString()
        return ""
    }
}
