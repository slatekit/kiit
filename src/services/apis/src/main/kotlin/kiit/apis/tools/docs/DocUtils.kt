package kiit.apis.tools.docs

import kiit.apis.ApiConstants
import kiit.apis.core.Part
import slatekit.common.args.ArgsCheck
import slatekit.requests.Request
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

object DocUtils {
    fun isHelp(request: Request): Outcome<Part> {
        return when {
            // Case 3a: Help ?
            ArgsCheck.isHelp(request.parts, 0) -> Outcomes.success(Part.All)
            // Case 3b: Help on area ?
            ArgsCheck.isHelp(request.parts, 1) -> Outcomes.success(Part.Area)
            // Case 3c: Help on api ?
            ArgsCheck.isHelp(request.parts, 2) -> Outcomes.success(Part.Api)
            // Case 3d: Help on action ?
            ArgsCheck.isHelp(request.parts, 3) -> Outcomes.success(Part.Action)
            else -> Outcomes.errored("Unknown help option")
        }
    }

    fun hasDocKey(request: Request, docKey: String): Boolean {
        // Ensure that docs are only available w/ help key
        val docKeyValue = when {
            request.meta.containsKey(ApiConstants.docKeyName) -> request.meta.get(ApiConstants.docKeyName) ?: ""
            request.data.containsKey(ApiConstants.docKeyName) -> request.data.get(ApiConstants.docKeyName) ?: ""
            else -> ""
        }
        return docKeyValue == docKey
    }
}
