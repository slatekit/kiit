package slatekit.apis.middleware

import slatekit.apis.ApiRequest
import slatekit.apis.core.Rewriter
import slatekit.common.validations.ValidationFuncs

class Restify : Rewriter() {
    private val verbGet = "get"
    private val verbPost = "post"
    private val verbPut = "put"
    private val verbDelete = "delete"
    private val verbPatch = "patch"

    /**
     * Rewrites restful routes and maps them to SlateKit API routes
     */
    override fun rewrite(request: ApiRequest): ApiRequest {

        // Get the first and second part
        val req = request.request
        val verb = req.verb.toLowerCase()
        val container = request.host
        return if (verb == verbGet && req.parts[2] == "") {
            rewriteAction(request, container.rename("getAll"))
        } else if (verb == verbGet && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(request, container.rename("getById"), "id", req.parts[2])
        } else if (verb == verbPost && req.parts[2] == "") {
            rewriteAction(request, container.rename("create"))
        } else if (verb == verbPut && req.parts[2] == "") {
            rewriteAction(request, container.rename("update"))
        } else if (verb == verbPatch && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(request, container.rename("patch"), "id", req.parts[2])
        } else if (verb == verbDelete && req.parts[2] == "") {
            rewriteAction(request, container.rename(verbDelete))
        } else if (verb == verbDelete && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(request, container.rename("deleteById"), "id", req.parts[2])
        } else {
            request
        }
    }
}
