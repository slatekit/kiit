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

package slatekit.apis.tools.docs

import slatekit.apis.Input
import slatekit.apis.core.Api
import slatekit.apis.core.Action
import kotlin.reflect.KParameter

interface ApiVisit {

    val docSettings: DocSettings

    fun onApiError(msg: String)

    fun onVisitSeparator()

    fun onAreasBegin()

    fun onAreaBegin(area: String)

    fun onAreaEnd(area: String)

    fun onAreasEnd()

    fun onApisBegin(area: String)

    fun onApiBegin(api: Api, options: ApiVisitOptions? = null)

    fun onApiBeginDetail(api: Api, options: ApiVisitOptions? = null)

    fun onApiEnd(api: Api)

    fun onApiActionSyntax(action: Action?)

    fun onApisEnd(area: String, exampleApi: String?)

    fun onApiActionBegin(api: Api, action: Action, name: String, options: ApiVisitOptions? = null)

    fun onApiActionBeginDetail(api: Api, action: Action, name: String, options: ApiVisitOptions? = null)

    fun onApiActionEnd(action: Action, name: String)

    fun onApiActionExample(api: Api, actionName: String, action: Action, args: List<KParameter>)

    fun onArgsBegin(action: Action)

    fun onArgBegin(arg: Input, options: ApiVisitOptions? = null)

    fun onArgBegin(
        name: String = "",
        desc: String = "",
        required: Boolean = true,
        type: String = "",
        defaultVal: String = "",
        eg: String = "",
        options: ApiVisitOptions? = null
    )

    fun onArgEnd(arg: Input)
}
