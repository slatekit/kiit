package slatekit.apis.helpers

import slatekit.apis.ApiConstants
import slatekit.common.args.ArgsCheck
import slatekit.common.requests.Request
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

fun Request.isHelp(): Outcome<String> {

    // Case 3a: Help ?
    return if (ArgsCheck.isHelp(this.parts, 0)) {
        Outcomes.success("?")
    }
    // Case 3b: Help on area ?
    else if (ArgsCheck.isHelp(this.parts, 1)) {
        Outcomes.success("area ?")
    }
    // Case 3c: Help on api ?
    else if (ArgsCheck.isHelp(this.parts, 2)) {
        Outcomes.success("area.api ?")
    }
    // Case 3d: Help on action ?
    else if (ArgsCheck.isHelp(this.parts, 3)) {
        Success("area.api.action ?")
    } else {
        Outcomes.errored("Unknown help option")
    }
}



fun Request.hasDocKey(docKey: String): Boolean {
    // Ensure that docs are only available w/ help key
    val docKeyValue = if (this.meta.containsKey(ApiConstants.docKeyName)) {
        this.meta.get(ApiConstants.docKeyName) ?: ""
    } else if (this.data.containsKey(ApiConstants.docKeyName)) {
        this.data.get(ApiConstants.docKeyName) ?: ""
    } else
        ""
    return docKeyValue == docKey
}