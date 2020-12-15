package slatekit.apis

import slatekit.apis.services.Help
import slatekit.apis.routes.Routes
import slatekit.apis.routes.Api
import slatekit.common.Source
import slatekit.common.naming.Namer

typealias API = Api

data class ApiContext(
    val source: Source,
    val apis: List<API>,
    val routes: Routes,
    val namer: Namer?,
    val help: Help
)
