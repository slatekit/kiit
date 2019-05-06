package test.setup

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.entities.EntityService
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext

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
    : ApiBaseEntity<Long ,Movie, EntityService<Long, Movie>>(ctx, Long::class, Movie::class, ctx.ent.getSvc(Movie::class)) {

    fun patch(id:Long, title:String): String = "patched $id with $title"
}


@Api(area = "app", name = "tests", desc = "sample to test compositional apis with annotations", roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class SampleEntity2Api(ctx: AppEntContext)
    : ApiBaseEntity<Long, Movie, EntityService<Long, Movie>>(ctx, Long::class, Movie::class, ctx.ent.getSvc(Movie::class)) {

    @ApiAction(name = "", desc = "gets the total number of users", roles = "@parent", verb = "post", protocol = "@parent")
    fun patch(id:Long, title:String): String = "patched $id with $title"
}
