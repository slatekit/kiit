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

import slatekit.apis.ApiHost
import slatekit.apis.doc.ApiVisitOptions
import slatekit.apis.doc.ApiVisitor
import slatekit.apis.doc.Doc

class Help(val host: ApiHost, val routes: Routes, val docBuilder: () -> Doc) {

    /**
     * handles help request for all the areas supported
     *
     * @return
     */
    fun help(): String {

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
