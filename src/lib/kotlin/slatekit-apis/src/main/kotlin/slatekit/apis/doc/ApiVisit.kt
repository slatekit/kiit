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

import slatekit.apis.ApiArg
import slatekit.apis.core.Api
import slatekit.apis.core.Action
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

    fun onApiBegin(api: Api, options: ApiVisitOptions? = null): Unit

    fun onApiBeginDetail(api: Api, options: ApiVisitOptions? = null): Unit

    fun onApiEnd(api: Api): Unit

    fun onApiActionSyntax(action: Action?): Unit

    fun onApisEnd(area: String, exampleApi: String?): Unit

    fun onApiActionBegin(api: Api, action: Action, name: String, options: ApiVisitOptions? = null): Unit

    fun onApiActionBeginDetail(api: Api, action: Action, name: String, options: ApiVisitOptions? = null): Unit

    fun onApiActionEnd(action: Action, name: String): Unit

    fun onApiActionExample(api: Api, actionName: String, action: Action, args: List<KParameter>): Unit

    fun onArgsBegin(action: Action): Unit

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
