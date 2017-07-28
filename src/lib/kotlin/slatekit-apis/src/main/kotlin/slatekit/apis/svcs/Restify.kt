package slatekit.apis.svcs

import slatekit.apis.ApiContainer
import slatekit.apis.middleware.Match
import slatekit.apis.middleware.Rewriter
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.info.About
import slatekit.common.validations.ValidationFuncs


class Restify : Rewriter(
        About.simple("slatekit.restify", "SlateKit.Restify", "Restful routes", "codehelix", "1.0"),
        Match("", "", ""))
{

    /**
     * Rewrites restful routes and maps them to SlateKit API routes
     */
    override fun rewrite(ctx: Context, req: Request, source: Any, args: Map<String, Any>): Request {

        // Get the first and second part
        val verb = req.verb
        val container = source as ApiContainer
        return if(verb == "get" && req.parts[2] == "") {
            rewriteAction(req, container.rename("getAll"))
        }
        else if (verb == "get" && ValidationFuncs.isNumeric(req.parts[2])){
            rewriteActionWithParam(req, container.rename("getById"), "id", req.parts[2])
        }
        else if(verb == "post" && req.parts[2] == "") {
            rewriteAction(req, container.rename("create"))
        }
        else if(verb == "put" && req.parts[2] == "") {
            rewriteAction(req, container.rename("update"))
        }
        else if(verb == "patch" && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(req, container.rename("patch"), "id", req.parts[2])
        }
        else if(verb == "delete" && req.parts[2] == "") {
            rewriteAction(req, container.rename("delete"))
        }
        else if(verb == "delete" && ValidationFuncs.isNumeric(req.parts[2])) {
            rewriteActionWithParam(req, container.rename("deleteById"), "id", req.parts[2])
        }
        else {
            req
        }
    }
}