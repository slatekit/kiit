package kiit.apis.tools.code

import kiit.apis.Access
import kiit.apis.routes.Action
import kiit.apis.routes.Api
import slatekit.common.Source

class CodeRules(val settings: CodeGenSettings) {

    fun isValidApi(api: Api): Boolean {
        val isValidProtocol = api.protocol == Source.API || api.protocol == Source.All
        val isValidAccess = api.access == Access.Public
        return isValidAccess && isValidProtocol
    }


    fun isValidAction(api: Api, action: Action, declaredMemberLookup: Map<String, Boolean>):Boolean {
        // Only include declared items
        val isValidProtocol = action.protocol == Source.API || action.protocol == Source.All
        val isValidAccess = action.access == Access.Public
        val isDeclared = declaredMemberLookup.containsKey(action.name)
        return isValidProtocol && isValidAccess && ( !this.settings.declaredMethodsOnly || isDeclared )
    }
}
