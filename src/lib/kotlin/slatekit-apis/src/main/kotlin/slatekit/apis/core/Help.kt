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

import slatekit.apis.ApiContainer
import slatekit.apis.doc.ApiVisitor
import slatekit.apis.doc.Doc


class Help(val ctn: ApiContainer, val routes: Routes, val docBuilder: () -> Doc) {


    /**
     * handles help request for all the areas supported
     *
     * @return
     */
    fun help(): String {

        val doc = docBuilder()
        val visitor = ApiVisitor()
        val apis = routes.areas
        apis?.let { apis ->
            visitor.visitAreas(apis, doc)
        }
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
        val visitor = ApiVisitor()
        visitor.visitApis(area, routes, doc)
        return doc.toString()
    }


    /**
     * handles help request for a specific api
     *
     * @param area
     * @param apiName
     * @return
     */
    fun api(area: String, apiName: String): String {
        val doc = docBuilder()
                val visitor = ApiVisitor()
                visitor.visitApiActions(apiBase, apiName, doc)

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
    fun action(area: String, api: String, action: String): String {
        val doc = docBuilder()
        val visitor = ApiVisitor()
        visitor.visitApiAction(api, api, action, doc)
        return doc.toString()
    }
}
