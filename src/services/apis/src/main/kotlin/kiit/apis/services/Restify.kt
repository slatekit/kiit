package kiit.apis.services

import kiit.apis.ApiRequest
import kiit.apis.ApiServer
import kiit.apis.support.RewriteSupport
import slatekit.common.checks.Check

open class Restify : Rewriter, RewriteSupport {

    private val verbGet = "get"
    private val verbPost = "post"
    private val verbPut = "put"
    private val verbDelete = "delete"
    private val verbPatch = "patch"

    /**
     * Rewrites restful routes and maps them to SlateKit API routes
     */
    override suspend fun process(req: ApiRequest): ApiRequest {

        // Get the first and second part
        val parts = req.request.parts
        val verb = req.request.verb.toLowerCase()
        val container = req.host

        val result = if (verb == verbGet && parts[2] == "") {
            rewrite(req, rename(container,"getAll"))
        } else if (verb == verbGet && Check.isNumeric(parts[2])) {
            rewriteWithParam(req, rename(container,"getById"), "id", parts[2])
        } else if (verb == verbPost && parts[2] == "") {
                rewrite(req, rename(container,"create"))
        } else if (verb == verbPut && parts[2] == "") {
                rewrite(req, rename(container,"update"))
        } else if (verb == verbPatch && Check.isNumeric(parts[2])) {
                rewriteWithParam(req, rename(container,"patch"), "id", parts[2])
        } else if (verb == verbDelete && parts[2] == "") {
                rewrite(req, rename(container,verbDelete))
        } else if (verb == verbDelete && Check.isNumeric(parts[2])) {
                rewriteWithParam(req, rename(container,"deleteById"), "id", parts[2])
        } else {
            req
        }
        return result
    }

    /**
     * Provides access to naming conventions used for actions
     */
    open fun rename(server:ApiServer, text: String): String = server.settings.naming?.rename(text) ?: text
}
