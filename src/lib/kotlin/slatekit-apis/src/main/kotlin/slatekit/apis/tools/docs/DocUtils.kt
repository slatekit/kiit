package slatekit.apis.tools.docs

import slatekit.apis.ApiConstants
import slatekit.apis.core.HelpType
import slatekit.common.args.ArgsCheck
import slatekit.common.requests.Request
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

object DocUtils {
    fun isHelp(request: Request): Outcome<HelpType> {

        // Case 3a: Help ?
        return if (ArgsCheck.isHelp(request.parts, 0)) {
            Outcomes.success(HelpType.All)
        }
        // Case 3b: Help on area ?
        else if (ArgsCheck.isHelp(request.parts, 1)) {
            Outcomes.success(HelpType.Area)
        }
        // Case 3c: Help on api ?
        else if (ArgsCheck.isHelp(request.parts, 2)) {
            Outcomes.success(HelpType.Api)
        }
        // Case 3d: Help on action ?
        else if (ArgsCheck.isHelp(request.parts, 3)) {
            Outcomes.success(HelpType.Action)
        } else {
            Outcomes.errored("Unknown help option")
        }
    }

    fun hasDocKey(request: Request, docKey: String): Boolean {
        // Ensure that docs are only available w/ help key
        val docKeyValue = if (request.meta.containsKey(ApiConstants.docKeyName)) {
            request.meta.get(ApiConstants.docKeyName) ?: ""
        } else if (request.data.containsKey(ApiConstants.docKeyName)) {
            request.data.get(ApiConstants.docKeyName) ?: ""
        } else
            ""
        return docKeyValue == docKey
    }
}
