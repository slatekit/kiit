package slatekit.apis.tools.code

import slatekit.apis.Access
import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.common.Source

class CodeGenRules(val settings: CodeGenSettings) {

    fun isValidApi(api: Api): Boolean {
        val isValidProtocol = api.protocol == Source.Web || api.protocol == Source.All
        val isValidAccess = api.access == Access.Public
        return isValidAccess && isValidProtocol
    }


    fun isValidAction(api: Api, action:Action, declaredMemberLookup: Map<String, Boolean>):Boolean {
        // Only include declared items
        val isDeclared = declaredMemberLookup.containsKey(action.name)
        return !this.settings.declaredMethodsOnly || isDeclared
    }
}
