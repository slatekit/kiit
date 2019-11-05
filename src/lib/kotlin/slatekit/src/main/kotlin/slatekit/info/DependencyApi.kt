package slatekit.info

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.common.Sources
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext
import slatekit.results.Outcome


@Api(area = "slate", name = "components", desc= "new project setup",
        auth = AuthModes.Keyed, roles = ["*"], verb = Verbs.Auto, sources = [Sources.CLI])
class DependencyApi(context: AppEntContext)
    : ApiBaseEntity<Long, Dependency, DependencyService>(
        context, Long::class, Dependency::class,
        context.ent.getSvc<Long, Dependency>(Dependency::class) as DependencyService) {

    @Action(desc= "generates the defaults/seed data")
    fun seed(): Outcome<List<String>>{
        return service.seed()
    }
}
