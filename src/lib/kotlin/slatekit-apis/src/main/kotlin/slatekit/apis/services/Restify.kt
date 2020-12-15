package slatekit.apis.services

import slatekit.apis.ApiRequest
import slatekit.apis.Middleware
import slatekit.apis.support.RewriteSupport
import slatekit.common.Ignore
import slatekit.common.validations.ValidationFuncs
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

class Restify : RewriteSupport, Middleware {

    private val verbGet = "get"
    private val verbPost = "post"
    private val verbPut = "put"
    private val verbDelete = "delete"
    private val verbPatch = "patch"

    /**
     * Rewrites restful routes and maps them to SlateKit API routes
     */
    @Ignore
    override suspend fun process(req: ApiRequest, next:suspend(ApiRequest) -> Outcome<Any>): Outcome<Any> {

        // Get the first and second part
        val parts = req.request.parts
        val verb = req.request.verb.toLowerCase()
        val container = req.host

        val result = if (verb == verbGet && parts[2] == "") {
            Outcomes.of { rewrite(req, container.rename("getAll")) }
        } else if (verb == verbGet && ValidationFuncs.isNumeric(parts[2])) {
            Outcomes.of { rewriteWithParam(req, container.rename("getById"), "id", parts[2]) }
        } else if (verb == verbPost && parts[2] == "") {
                Outcomes.of { rewrite(req, container.rename("create")) }
        } else if (verb == verbPut && parts[2] == "") {
                Outcomes.of { rewrite(req, container.rename("update")) }
        } else if (verb == verbPatch && ValidationFuncs.isNumeric(parts[2])) {
                Outcomes.of { rewriteWithParam(req, container.rename("patch"), "id", parts[2]) }
        } else if (verb == verbDelete && parts[2] == "") {
                Outcomes.of { rewrite(req, container.rename(verbDelete)) }
        } else if (verb == verbDelete && ValidationFuncs.isNumeric(parts[2])) {
                Outcomes.of { rewriteWithParam(req, container.rename("deleteById"), "id", parts[2]) }
        } else {
            next(req)
        }
        return result
    }
}
