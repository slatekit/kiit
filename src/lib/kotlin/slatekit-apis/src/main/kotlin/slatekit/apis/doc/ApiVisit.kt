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

import slatekit.apis.ApiAction
import slatekit.apis.ApiArg
import slatekit.apis.core.Action
import slatekit.apis.support.ApiInfo
import kotlin.reflect.KParameter


interface ApiVisit {

    val docSettings: DocSettings


    fun onApiError(msg: String): Unit


    fun onVisitSeparator(): Unit


    fun onAreasBegin(): Unit


    fun onAreaBegin(area: String): Unit


    fun onAreaEnd(area: String): Unit


    fun onAreasEnd(): Unit


    fun onApisBegin(area: String): Unit


    fun onApiBegin(api: ApiInfo, options: ApiVisitOptions? = null): Unit


    fun onApiEnd(api: ApiInfo): Unit


    fun onApiActionSyntax(action: Action?): Unit


    fun onApisEnd(area: String, exampleApi: String?): Unit


    fun onApiActionBegin(action: ApiAction, name: String, options: ApiVisitOptions? = null): Unit


    fun onApiActionEnd(action: ApiAction, name: String): Unit


    fun onApiActionExample(api: ApiInfo, actionName: String, action: ApiAction, args: List<KParameter>): Unit


    fun onArgBegin(arg: ApiArg, options: ApiVisitOptions? = null): Unit


    fun onArgBegin(
            name: String = "",
            desc: String = "",
            required: Boolean = true,
            type: String = "",
            defaultVal: String = "",
            eg: String = "",
            options: ApiVisitOptions? = null
    )


    fun onArgEnd(arg: ApiArg): Unit
}
