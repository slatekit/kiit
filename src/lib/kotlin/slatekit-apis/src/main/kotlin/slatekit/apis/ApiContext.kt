package slatekit.apis

import slatekit.apis.core.Help
import slatekit.apis.core.Routes
import slatekit.apis.setup.Protocol
import slatekit.common.naming.Namer

typealias API = slatekit.apis.core.Api


data class ApiContext(val protocol: Protocol,
                      val apis: List<API>,
                      val routes:Routes,
                      val namer: Namer?,
                      val help: Help)