package slatekit.apis.tools.code

import slatekit.apis.ApiServer
import slatekit.common.requests.Request

data class CodeGenSettings(
    val host: ApiServer,
    val req: Request,
    val templatesFolder: String,
    val outputFolder: String,
    val packageName: String,
    val lang: Language,
    val createDtos:Boolean = true,
    val templateClassSuffix :String = "Api",
    val declaredMethodsOnly: Boolean = true
)
