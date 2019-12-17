/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis.core

import slatekit.apis.ApiServer
import slatekit.apis.tools.docs.ApiVisitOptions
import slatekit.apis.tools.docs.ApiVisitor
import slatekit.apis.tools.docs.Doc
import slatekit.apis.tools.docs.DocUtils
import slatekit.common.types.Content
import slatekit.common.requests.Request
import slatekit.results.*
import slatekit.results.builders.Outcomes


sealed class HelpType {
    object All    : HelpType()
    object Area   : HelpType()
    object Api    : HelpType()
    object Action : HelpType()
}

class Help(val host: ApiServer, val routes: Routes, val docKey: String?, val docBuilder: () -> Doc) {

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
    fun build(req: Request, helpType: HelpType): Outcome<Content> {
        return if (!DocUtils.hasDocKey(req, docKey ?: "")) {
            Outcomes.denied("Unauthorized access to API docs")
        } else {
            val content = when (helpType) {
                // 1: ? = help on all
                HelpType.All -> {
                    areas()
                }
                // 2: {area} ? = help on area
                HelpType.Area -> {
                    area(req.parts[0])
                }
                // 3. {area}.{api} = help on api
                HelpType.Api -> {
                    api(req.parts[0], req.parts[1])
                }
                // 4. {area}.{api}.{action} = help on api
                HelpType.Action -> {
                    action(req.parts[0], req.parts[1], req.parts[2])
                }
            }
            Outcomes.success(Content.html(content))
        }
    }

    /**
     * handles help request for all the areas supported
     *
     * @return
     */
    fun areas(): String {
        val doc = docBuilder()
        val visitor = ApiVisitor(routes)
        visitor.visitAreas(doc)
        return doc.toString()
    }

    /**
     * handles help request for a specific area
     *
     * @param area
     * @return
     */
    fun area(area: String): String {
        val doc = docBuilder()
        val visitor = ApiVisitor(routes)
        visitor.visitApis(area, doc)
        return doc.toString()
    }

    /**
     * handles help request for a specific api
     *
     * @param area
     * @param apiName
     * @return
     */
    fun api(area: String, api: String): String {
        val doc = docBuilder()
        val visitor = ApiVisitor(routes)
        visitor.visitActions(area, api, doc)
        return doc.toString()
    }

    /**
     * handles help request for a specific api action
     *
     * @param area
     * @param apiName
     * @param actionName
     * @return
     */
    fun action(area: String, name: String, action: String): String {
        val doc = docBuilder()
        val visitor = ApiVisitor(routes)
        val api = routes.api(area, name)
        api?.actions?.get(action)?.let { act ->
            visitor.visitAction(api, act, doc, true, options = ApiVisitOptions())
        }
        return doc.toString()
    }
}
