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
import slatekit.apis.ApiReg
import slatekit.apis.ApiRegAction
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


    fun onApiBegin(api: ApiReg, options: ApiVisitOptions? = null): Unit


    fun onApiBeginDetail(api: ApiReg, options: ApiVisitOptions? = null): Unit


    fun onApiEnd(api: ApiReg): Unit


    fun onApiActionSyntax(action: ApiRegAction?): Unit


    fun onApisEnd(area: String, exampleApi: String?): Unit


    fun onApiActionBegin(action: ApiRegAction, name: String, options: ApiVisitOptions? = null): Unit


    fun onApiActionBeginDetail(action: ApiRegAction, name: String, options: ApiVisitOptions? = null): Unit


    fun onApiActionEnd(action: ApiRegAction, name: String): Unit


    fun onApiActionExample(api: ApiReg, actionName: String, action: ApiRegAction, args: List<KParameter>): Unit


    fun onArgsBegin(action: ApiRegAction): Unit


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
