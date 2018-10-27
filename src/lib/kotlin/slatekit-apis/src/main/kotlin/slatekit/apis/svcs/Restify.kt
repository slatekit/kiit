package slatekit.apis.svcs

import slatekit.apis.ApiContainer
import slatekit.apis.middleware.Rewriter
import slatekit.common.Context
import slatekit.common.Request
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
    override fun rewrite(ctx: Context, req: Request, source: Any, args: Map<String, Any>): Request {

        // Get the first and second part
        val verb = req.verb.toLowerCase()
        val container = source as ApiContainer
        return if (verb == verbGet && req.parts[2] == "") {
            rewriteAction(req, container.rename("getAll"))
        } else if (verb == verbGet && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(req, container.rename("getById"), "id", req.parts[2])
        } else if (verb == verbPost && req.parts[2] == "") {
            rewriteAction(req, container.rename("create"))
        } else if (verb == verbPut && req.parts[2] == "") {
            rewriteAction(req, container.rename("update"))
        } else if (verb == verbPatch && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(req, container.rename("patch"), "id", req.parts[2])
        } else if (verb == verbDelete && req.parts[2] == "") {
            rewriteAction(req, container.rename(verbDelete))
        } else if (verb == verbDelete && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(req, container.rename("deleteById"), "id", req.parts[2])
        } else {
            req
        }
    }
}
