package slatekit.apis.helpers

import slatekit.apis.ApiConstants
import slatekit.apis.setup.Protocols
import slatekit.common.requests.Request
import slatekit.common.args.ArgsCheck
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success

object ApiUtils {

    fun isHelp(req: Request): Notice<String> {

        // Case 3a: Help ?
        return if (ArgsCheck.isHelp(req.parts, 0)) {
            Success("?")
        }
        // Case 3b: Help on area ?
        else if (ArgsCheck.isHelp(req.parts, 1)) {
            Success("area ?")
        }
        // Case 3c: Help on api ?
        else if (ArgsCheck.isHelp(req.parts, 2)) {
            Success("area.api ?")
        }
        // Case 3d: Help on action ?
        else if (ArgsCheck.isHelp(req.parts, 3)) {
            Success("area.api.action ?")
        } else {
            Failure("Unknown help option")
        }
    }


    fun isDocKeyed(req: Request, docKey: String): Boolean {
        // Ensure that docs are only available w/ help key
        val docKeyValue = if (req.meta.containsKey(ApiConstants.DocKeyName)) {
            req.meta.get(ApiConstants.DocKeyName) ?: ""
        } else if (req.data.containsKey(ApiConstants.DocKeyName)) {
            req.data.get(ApiConstants.DocKeyName) ?: ""
        } else
            ""
        return docKeyValue == docKey
    }

    fun isCliAllowed(supportedProtocol: String): Boolean =
            supportedProtocol == Protocols.all || supportedProtocol == Protocols.cli


    fun getReferencedValue(primaryValue: String, parentValue: String): String {

        // Role!
        return if (!primaryValue.isNullOrEmpty()) {
            if (primaryValue == ApiConstants.parent) {
                parentValue
            } else
                primaryValue
        }
        // Parent!
        else if (!parentValue.isNullOrEmpty()) {
            parentValue
        } else
            ""
    }
}