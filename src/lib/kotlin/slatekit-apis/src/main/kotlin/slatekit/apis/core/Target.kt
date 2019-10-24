package slatekit.apis.core

import slatekit.apis.core.Action
import slatekit.apis.core.Api

data class Target(val api: Api, val action: Action, val instance: Any)