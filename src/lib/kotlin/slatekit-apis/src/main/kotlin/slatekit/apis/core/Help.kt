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
import slatekit.apis.helpers.Areas


class Help(val ctn: ApiContainer, val lookup: Areas, val docBuilder: () -> Doc) {


    /**
     * handles help request for all the areas supported
     *
     * @return
     */
    fun help(): String {

        val doc = docBuilder()
        val visitor = ApiVisitor()
        val apis = lookup.keys()
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
    fun helpForArea(area: String): String {
        val doc = docBuilder()
        val visitor = ApiVisitor()
        visitor.visitApis(area, lookup, doc)
        return doc.toString()
    }


    /**
     * handles help request for a specific api
     *
     * @param area
     * @param apiName
     * @return
     */
    fun helpForApi(area: String, apiName: String): String {
        val doc = docBuilder()
        val apis = lookup[area]
        apis?.let { apis ->
            val api = apis[apiName]
            api?.let { apiBase ->
                val visitor = ApiVisitor()
                visitor.visitApiActions(apiBase, apiName, doc)
            }
        }
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
    fun helpForAction(area: String, apiName: String, actionName: String): String {
        val doc = docBuilder()
        val apis = lookup[area]
        apis?.let { apis ->
            val api = apis[apiName]
            api?.let { apiBase ->
                val visitor = ApiVisitor()
                visitor.visitApiAction(apiBase, apiName, actionName, doc)
            }
        }
        return doc.toString()
    }
}
