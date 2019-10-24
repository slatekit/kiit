package slatekit.apis.middleware

import slatekit.apis.ApiRequest
import slatekit.common.*
import slatekit.apis.core.Rewriter
import slatekit.common.content.ContentTypeCsv
import slatekit.common.content.ContentTypeJson
import slatekit.common.content.ContentTypeProp
import slatekit.common.requests.Request

class Format : Rewriter() {

    /**
     * Rewrites restful routes and maps them to SlateKit API routes
     */
    override fun rewrite(req: ApiRequest): ApiRequest {

        // Update request if formats are supplied
        // 1. movies.json
        // 2. movies.csv
        // 3. movies.props
        val rawAction = req.request.action
        val indexPeriod = rawAction.indexOf("")
        val action = if (indexPeriod > 0) rawAction.substring(0, indexPeriod) else rawAction
        val suffix = if (indexPeriod > 0) rawAction.substring(indexPeriod + 1).toLowerCase() else ""

        return when (suffix) {
            ContentTypeCsv.ext ->  rewriteAction(req, action, ContentTypeCsv.ext)
            ContentTypeJson.ext -> rewriteAction(req, action, ContentTypeJson.ext)
            ContentTypeProp.ext -> rewriteAction(req, action, ContentTypeProp.ext)
            else -> req
        }
    }
}
