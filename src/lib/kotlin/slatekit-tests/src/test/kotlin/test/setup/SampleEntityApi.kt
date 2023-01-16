package test.setup

import slatekit.apis.Api
import slatekit.apis.Action
import kiit.entities.EntityService
import slatekit.integration.common.ApiBaseEntity
import slatekit.connectors.entities.AppEntContext
import kiit.entities.features.Counts
import kiit.entities.features.Ordered

/**
 * REST Sample
 * This example shows a REST compliant API.
 * The Slate Kit API Container comes with middle-ware. One of the
 * middle-ware components is the Rewrite which can convert 1 request
 * to another request. Using this Rewrite component, we can customize
 * and enforce conventions.
 *
 *
 * NOTES:
 * 1. REST        : REST Support for methods with 0 or 1 parameters.
 * 2. Conventions : Create your own conventions using the Rewrite component
 *
 * HTTP:
 *      Method   Route
 *      GET      /SampleREST/                =>   getAll
 *      GET      /SampleREST/1               =>   getById   ( 1 )
 *      POST     /SampleREST/{item}          =>   create    ( item )
 *      PUT      /SampleREST/{item}          =>   update    ( item )
 *      DELETE   /SampleREST/{item}          =>   delete    ( item )
 *      DELETE   /SampleREST/1               =>   deleteById( 1 )
 *      PATCH    /SampleREST?id=1&title=abc  =>   patch     ( id, title )
 *
 *
 * CLI:
 *      SampleREST.getAll
 *      SampleREST.getById    -id=1
 *      SampleREST.create     -title="abc" -category="action"
 *      SampleREST.update     -id=1 -title="abc" -category="action"
 *      SampleREST.delete     -id=1 -title="abc" -category="action"
 *      SampleREST.deleteById -id=1
 *      SampleREST.patch      -id=1 -title="abc"
 */
class SampleEntityApi(ctx: AppEntContext)
    : ApiBaseEntity<Long ,Movie, EntityService<Long, Movie>>(ctx, Long::class, Movie::class, ctx.ent.getService()) {

    fun patch(id:Long, title:String): String = "patched $id with $title"
}


@Api(area = "app", name = "tests", desc = "sample to test compositional apis with annotations", roles= ["admin"])
class SampleEntity2Api(ctx: AppEntContext){
    private val svc = ctx.ent.getService<Long, Movie>() as Ordered<Long ,Movie>

    @Action(name = "", desc = "gets the total number of users")
    fun patch(id:Long, title:String): String = "patched $id with $title"

    @Action(name = "", desc = "gets recent items in the system")
    suspend fun recent(count: Int = 5): List<Movie> {
        return svc.recent(count)
    }

    @Action(name = "", desc = "gets oldest items in the system")
    suspend fun oldest(count: Int = 5): List<Movie> {
        return svc.oldest(count)
    }
}
