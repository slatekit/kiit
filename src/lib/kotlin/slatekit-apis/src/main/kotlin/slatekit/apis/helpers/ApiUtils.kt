package slatekit.apis.helpers

import slatekit.apis.ApiConstants
import slatekit.apis.security.Protocols
import slatekit.common.Request
import slatekit.common.ResultMsg
import slatekit.common.Success
import slatekit.common.args.ArgsFuncs
import slatekit.common.content.Content
import slatekit.common.content.ContentTypeCsv
import slatekit.common.content.ContentTypeJson
import slatekit.common.content.ContentTypeProp
import slatekit.common.getOrElse
import slatekit.common.results.ResultFuncs
import slatekit.meta.Serialization

object ApiUtils {

    fun isHelp(req: Request): ResultMsg<String> {

        // Case 3a: Help ?
        return if (ArgsFuncs.isHelp(req.parts, 0)) {
            ResultFuncs.help(msg = "?")
        }
        // Case 3b: Help on area ?
        else if (ArgsFuncs.isHelp(req.parts, 1)) {
            ResultFuncs.help(msg = "area ?")
        }
        // Case 3c: Help on api ?
        else if (ArgsFuncs.isHelp(req.parts, 2)) {
            ResultFuncs.help(msg = "area.api ?")
        }
        // Case 3d: Help on action ?
        else if (ArgsFuncs.isHelp(req.parts, 3)) {
            ResultFuncs.help(msg = "area.api.action ?")
        } else {
            ResultFuncs.failure("Unknown help option")
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

}