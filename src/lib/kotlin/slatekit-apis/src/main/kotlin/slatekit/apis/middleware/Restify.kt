package slatekit.apis.middleware

import slatekit.apis.ApiRequest
import slatekit.apis.support.RewriteSupport
import slatekit.common.Ignore
import slatekit.common.validations.ValidationFuncs
import slatekit.functions.Input
import slatekit.results.Outcome
import slatekit.results.flatMap

class Restify : Input<ApiRequest>, RewriteSupport {

    private val verbGet = "get"
    private val verbPost = "post"
    private val verbPut = "put"
    private val verbDelete = "delete"
    private val verbPatch = "patch"

    /**
     * Rewrites restful routes and maps them to SlateKit API routes
     */
    @Ignore
    override suspend fun process(request:Outcome<ApiRequest>):Outcome<ApiRequest> {
        return request.flatMap {
            // Get the first and second part
            val req = it.request
            val verb = req.verb.toLowerCase()
            val container = it.host

            val result = if (verb == verbGet && req.parts[2] == "") {
                Outcome.of { rewriteAction(it, container.rename("getAll")) }
            } else if (verb == verbGet && ValidationFuncs.isNumeric(req.parts[2])) {
                Outcome.of {rewriteActionWithParam(it, container.rename("getById"), "id", req.parts[2]) }
            } else if (verb == verbPost && req.parts[2] == "") {
                    Outcome.of { rewriteAction(it, container.rename("create")) }
            } else if (verb == verbPut && req.parts[2] == "") {
                    Outcome.of { rewriteAction(it, container.rename("update")) }
            } else if (verb == verbPatch && ValidationFuncs.isNumeric(req.parts[2])) {
                    Outcome.of { rewriteActionWithParam(it, container.rename("patch"), "id", req.parts[2]) }
            } else if (verb == verbDelete && req.parts[2] == "") {
                    Outcome.of { rewriteAction(it, container.rename(verbDelete)) }
            } else if (verb == verbDelete && ValidationFuncs.isNumeric(req.parts[2])) {
                    Outcome.of { rewriteActionWithParam(it, container.rename("deleteById"), "id", req.parts[2]) }
            } else {
                request
            }
            result
        }
    }
}
