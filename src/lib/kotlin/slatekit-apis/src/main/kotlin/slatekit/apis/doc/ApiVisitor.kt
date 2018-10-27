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

import slatekit.apis.core.*
import slatekit.meta.KTypes

class ApiVisitor(val routes: Routes) {

    /**
     * Displays all the areas in the API container.
     */
    fun visitAreas(visitor: ApiVisit) {
        visitor.onAreasBegin()
        routes.areas.items.forEach { area ->
            visitor.onAreaBegin(area.name)
            visitor.onAreaEnd(area.name)
        }
        visitor.onAreasEnd()
    }

    /**
     * Displays all the APIs in the areas supplied.
     */
    fun visitApis(area: String, visitor: ApiVisit) {
        if (!routes.contains(area)) {
            visitor.onApiError("Area : $area not found")
        } else {
            val apis = routes.areas.get(area)?.apis ?: Lookup<Api>(listOf(), { api -> api.name })
            if (apis.size > 0) {
                val maxLength = apis.items.maxBy { it.name.length }?.name?.length ?: 10
                val options = ApiVisitOptions(maxLength, true)

                // 1. Begin the area
                visitor.onApisBegin(area)

                // 2. Sort by name
                val sorted = apis.items.sortedBy { it.name }

                // 3. Now get the api and print info
                sorted.forEach { api -> visitor.onApiBegin(api, options) }
                val lastApiName = sorted.last()
                visitor.onApisEnd(area, "$area.$lastApiName")
            }
        }
    }

    fun visitActions(area: String, name: String, visitor: ApiVisit) {
        if (!routes.contains(area, name)) {
            visitor.onApiError("API not found for $area.$name")
        }
        val api = routes.api(area, name)!!
        val actions = api.actions
        val first: Action? = actions.items.firstOrNull()
        first?.let { visitor.onApiBeginDetail(api) }
        if (actions.size > 0) {
                visitor.onVisitSeparator()
                val sortedActions = actions.items.sortedBy { s -> s.name }
                val maxLength = sortedActions.maxBy { it.name.length }?.name?.length ?: 10
                val options = ApiVisitOptions(maxLength)
                sortedActions.forEach { action ->
                    visitor.onApiActionBegin(api, action, action.name, options)
                }
        }
        visitor.onApiActionSyntax(first)
    }

    fun visitAction(api: Api, action: Action, visitor: ApiVisit, detailMode: Boolean = true, options: ApiVisitOptions?) {
        // action
        visitor.onApiActionBeginDetail(api, action, action.name, options)

        if (detailMode) {
            visitArgs(action, visitor)
        }
        // args here.
        visitor.onApiActionEnd(action, action.name)
    }

    fun visitArgs(info: Action, visitor: ApiVisit) {
        visitor.onArgsBegin(info)
        if (info.hasArgs) {
            val names = info.paramsUser.map { item -> item.name }.filterNotNull()
            val maxLength = names.maxBy { it.length }?.length ?: 10
            val options = ApiVisitOptions(maxLength)
            info.paramsUser.forEach { argInfo ->

                val clsType = KTypes.getClassFromType(argInfo.type)
                visitor.onArgBegin(argInfo.name!!, "", !argInfo.isOptional, clsType.simpleName!!, options = options)
            }
        }
    }
}
