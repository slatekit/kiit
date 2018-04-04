package slatekit.apis

import slatekit.apis.core.Action
import slatekit.apis.core.Api

data class ApiRef(val api: Api, val action: Action, val instance:Any)