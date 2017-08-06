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

package slatekit.apis.doc

import slatekit.apis.ApiReg
import slatekit.apis.ApiRegAction
import slatekit.apis.helpers.ApiLookup
import slatekit.apis.helpers.Areas
import slatekit.common.ListMap
import slatekit.meta.KTypes


class ApiVisitor {

    /**
     * Displays all the areas in the API container.
     */
    fun visitAreas(areas: List<String>, visitor: ApiVisit): Unit {
        visitor.onAreasBegin()
        areas.forEach { area ->
            visitor.onAreaBegin(area)
            visitor.onAreaEnd(area)
        }
        visitor.onAreasEnd()
    }


    /**
     * Displays all the APIs in the areas supplied.
     */
    fun visitApis(area: String, lookup: Areas, visitor: ApiVisit): Unit {
        val apis = lookup[area]
        if (apis == null) {
            visitor.onApiError("Area : $area not found")
        }
        else {
            val all = apis.all()
            if (all.isNotEmpty()) {
                val keys = apis.keys()
                val maxLength = keys.maxBy { it.length }?.length ?: 10
                val options = ApiVisitOptions(maxLength, true)

                // 1. Begin the area
                visitor.onApisBegin(area)

                // 2. Sort by name
                val sorted = keys.sortedBy { it }

                // 3. Now get the api and print info
                sorted.forEach { key ->
                    val api = apis[key]

                    api?.let { api ->

                        // 4. Print info
                        val apiInfo = lookup.getApiInfo(area, key)

                        visitor.onApiBegin(apiInfo!!, options)
                    }
                }
                val lastApiName = sorted.last()
                visitor.onApisEnd(area, "$area.$lastApiName")
            }
        }
    }


    fun visitApi(apiBase: ApiLookup, apiName: String, visitor: ApiVisit, listActions: Boolean = false): Unit {
        val actions = apiBase.actions()
        val first: ApiRegAction? = actions.all().firstOrNull()
        if (actions.size > 0) {
            actions.getAt(0)?.let { apiAnno ->
                visitApi(apiAnno.api, visitor, actions, listActions = listActions, listArgs = false)
            }
        }
        visitor.onApiActionSyntax(first)
    }


    fun visitApi(api: ApiReg, visitor: ApiVisit, actions: ListMap<String, ApiRegAction>,
                 listActions: Boolean = true, listArgs: Boolean = false): Unit {
        visitor.onApiBegin(api)
        if (actions.size > 0) {
            if (listActions) {

                visitor.onVisitSeparator()
                val actionNames = actions.keys().sortedBy { s -> s }
                val maxLength = actionNames.maxBy { it.length }?.length ?: 10
                val options = ApiVisitOptions(maxLength)
                actionNames.forEach { actionName ->
                    val action = actions[actionName]
                    action?.let { act ->
                        visitApiAction(act, visitor, listArgs, options)
                    }
                }
            }
        }
        visitor.onApiEnd(api)
    }


    fun visitApiAction(apiBase: ApiLookup, apiName: String, actionName: String, visitor: ApiVisit): Unit {
        val actions = apiBase.actions()
        if (actions.size > 0) {
            actions.getAt(0)?.let { apiAnno ->
                val api = apiAnno.api
                visitor.onApiBegin(api)
                visitor.onVisitSeparator()
                val call = actions[actionName]
                call?.let { action ->
                    visitApiAction(action, visitor, detailMode = true, options = null)

                    if (true) {
                        visitor.onApiActionExample(api, call.name, call, call.paramList)
                    }
                }
            }
        }
    }


    fun visitApiAction(action: ApiRegAction, visitor: ApiVisit, detailMode: Boolean = true, options: ApiVisitOptions?): Unit {
        // action
        visitor.onApiActionBegin(action, action.name, options)

        if (detailMode) {
            visitArgs(action, visitor)
        }
        // args here.
        visitor.onApiActionEnd(action, action.name)
    }


    fun visitArgs(info: ApiRegAction, visitor: ApiVisit): Unit {
        if (info.hasArgs) {
            val names = info.paramList.map { item -> item.name }.filterNotNull()
            val maxLength = names.maxBy { it.length }?.length ?: 10
            val options = ApiVisitOptions(maxLength)
            info.paramList.forEach { argInfo ->

                val clsType = KTypes.getClassFromType(argInfo.type)
                visitor.onArgBegin(argInfo.name!!, "", !argInfo.isOptional, clsType.simpleName!!, options = options)
            }
        }
    }
}
