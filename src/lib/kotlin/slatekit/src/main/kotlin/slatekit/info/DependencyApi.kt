package slatekit.info

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext
import slatekit.results.Outcome


@Api(area = "slate", name = "components", desc= "new project setup",
        auth = AuthModes.apiKey, roles = "*", verb = Verbs.auto, protocol = Protocols.cli)
class DependencyApi(context: AppEntContext)
    : ApiBaseEntity<Long, Dependency, DependencyService>(context, Long::class, Dependency::class) {

    @ApiAction(desc= "generates the defaults/seed data")
    fun seed(): Outcome<List<String>>{
        return service.seed()
    }
}
