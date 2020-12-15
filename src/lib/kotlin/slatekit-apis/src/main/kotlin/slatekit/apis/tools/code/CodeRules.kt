package slatekit.apis.tools.code

import slatekit.apis.Access
import slatekit.apis.routes.Action
import slatekit.apis.routes.Api
import slatekit.common.Source

class CodeRules(val settings: CodeGenSettings) {

    fun isValidApi(api: Api): Boolean {
        val isValidProtocol = api.protocol == Source.Web || api.protocol == Source.All
        val isValidAccess = api.access == Access.Public
        return isValidAccess && isValidProtocol
    }


    fun isValidAction(api: Api, action: Action, declaredMemberLookup: Map<String, Boolean>):Boolean {
        // Only include declared items
        val isValidProtocol = action.protocol == Source.Web || action.protocol == Source.All
        val isValidAccess = action.access == Access.Public
        val isDeclared = declaredMemberLookup.containsKey(action.name)
        return isValidProtocol && isValidAccess && ( !this.settings.declaredMethodsOnly || isDeclared )
    }
}
