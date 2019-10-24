package slatekit.apis.middleware

import slatekit.apis.ApiRequest
import slatekit.apis.support.RewriteSupport
import slatekit.common.Ignore
import slatekit.common.content.ContentTypeCsv
import slatekit.common.content.ContentTypeJson
import slatekit.common.content.ContentTypeProp
import slatekit.functions.Input
import slatekit.results.Outcome
import slatekit.results.flatMap

class Formats : Input<ApiRequest>, RewriteSupport {

    @Ignore
    override suspend fun process(req:Outcome<ApiRequest>):Outcome<ApiRequest> {
        return req.flatMap {
            // Update request if formats are supplied
            // 1. movies.json
            // 2. movies.csv
            // 3. movies.props
            val rawAction = it.request.action
            val indexPeriod = rawAction.indexOf("")
            val action = if (indexPeriod > 0) rawAction.substring(0, indexPeriod) else rawAction
            val suffix = if (indexPeriod > 0) rawAction.substring(indexPeriod + 1).toLowerCase() else ""

            val result = when (suffix) {
                ContentTypeCsv.ext ->  Outcome.of { rewriteAction (it, action, ContentTypeCsv.ext) }
                ContentTypeJson.ext -> Outcome.of { rewriteAction(it, action, ContentTypeJson.ext) }
                ContentTypeProp.ext -> Outcome.of { rewriteAction(it, action, ContentTypeProp.ext) }
                else -> req
            }
            result
        }
    }
}
