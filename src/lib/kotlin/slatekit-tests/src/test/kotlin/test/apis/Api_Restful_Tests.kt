/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.apis

import org.json.simple.JSONObject
import org.junit.Assert
import org.junit.Test
import slatekit.apis.*
import slatekit.apis.core.Api
import slatekit.apis.svcs.Restify
import slatekit.common.*
import slatekit.common.ext.toStringYYYYMMDD
import slatekit.common.naming.LowerHyphenNamer
import slatekit.common.naming.Namer
import slatekit.results.Result
import slatekit.results.StatusCodes
import slatekit.results.getOrElse
import test.setup.SampleRESTApi
import test.setup.Movie

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Restful_Tests : ApiTestsBase() {

/*
* GET    /tickets    - Retrieves a list of tickets
* GET    /tickets/12 - Retrieves a specific ticket
* POST   /tickets    - Creates a new ticket
* PUT    /tickets/12 - Updates ticket #12
* PATCH  /tickets/12 - Partially updates ticket #12
* DELETE /tickets/12 - Deletes ticket #12
*
*/
    @Test fun can_get_all() {

       ensure("", "get", mapOf(), namer = LowerHyphenNamer(), callback ={ r1 ->

            Assert.assertTrue(r1.success)
            Assert.assertTrue(r1.code == StatusCodes.SUCCESS.code)

            val all = r1.getOrElse { Movie.samples() } as List<Movie>
           Assert.assertTrue(all.size == 2 )
        })
    }


    @Test fun can_get_by_id() {

        val apis = ApiContainer(ctx, apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")), auth = null, allowIO = false, middleware = listOf(Restify()))
        val r1 = apis.call("app", "SampleREST", "1", "get", mapOf(), mapOf())

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == StatusCodes.SUCCESS.code)

        val book = r1.getOrElse { Movie.samples()[0] } as Movie
        Assert.assertTrue(book.title == Movie.samples().first().title)
    }


    @Test fun can_patch() {

        val apis = ApiContainer(ctx, apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")), auth = null, allowIO = false, middleware = listOf(Restify()))
        val r1 = apis.call("app", "SampleREST", "1", "patch", mapOf(),
                mapOf("title" to "Indiana Jones Original"))

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == StatusCodes.SUCCESS.code)
        Assert.assertTrue(r1.getOrElse { "" } == "patched 1 with Indiana Jones Original")
    }


    @Test fun can_delete_by_id() {

        val apis = ApiContainer(ctx, apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")), auth = null, allowIO = false, middleware = listOf(Restify()))
        val r1 = apis.call("app", "SampleREST", "1", "delete", mapOf(), mapOf())

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == StatusCodes.SUCCESS.code)
        Assert.assertTrue(r1.getOrElse { "" } == "deleteById 1")
    }


    @Test fun can_activate_by_id() {

        val apis = ApiContainer(ctx, apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")), auth = null, allowIO = false, middleware = listOf(Restify()))
        val r1 = apis.call("app", "SampleREST", "activateById", "post", mapOf(), mapOf("id" to 1))

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == StatusCodes.SUCCESS.code)
        Assert.assertTrue(r1.getOrElse { "" } == "activateById 1")
    }


    @Test
    fun can_create(){
        val json = JSONObject()
        json.put("id"        , "0")
        json.put("title"     , "Indiana Jones")
        json.put("category"  , "adventure")
        json.put("playing"   , "false")
        json.put("cost"      , "30")
        json.put("rating"    , "4.8")
        json.put("released"  , "19810612")
        json.put("createdAt" , DateTimes.of(2017, 7, 17).toStringYYYYMMDD(""))
        json.put("createdBy" , "0")
        json.put("updatedAt" , DateTimes.of(2017, 7, 17).toStringYYYYMMDD(""))
        json.put("updatedBy" , "0")
        val data = mapOf( "item" to json )
        val apis = ApiContainer(ctx,
                apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")),
                auth = null, allowIO = false,
                middleware = listOf(Restify()))
        val r1 = apis.call(
                "app", "SampleREST", "", "post",
                mapOf("api-key" to "3E35584A8DE0460BB28D6E0D32FB4CFD"),
                data
        )

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == StatusCodes.SUCCESS.code)
        Assert.assertTrue(r1.getOrElse { 0L } == 3L)
    }


    @Test
    fun can_update(){

        val json = JSONObject()
        json.put("id"        , "1")
        json.put("title"     , "Indiana Jones")
        json.put("category"  , "adventure")
        json.put("playing"   , "false")
        json.put("cost"      , "30")
        json.put("rating"    , "4.8")
        json.put("released"  , "19810612")
        json.put("createdAt" , DateTimes.of(2017, 7, 17).toStringYYYYMMDD(""))
        json.put("createdBy" , "0")
        json.put("updatedAt" , DateTimes.of(2017, 7, 17).toStringYYYYMMDD(""))
        json.put("updatedBy" , "0")
        val data = mapOf( "item" to json )
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")), auth = null, allowIO = false,  middleware = listOf(Restify()))
        val r1 = apis.call(
                "app", "SampleREST", "", "put",
                mapOf("api-key" to "3E35584A8DE0460BB28D6E0D32FB4CFD"),
                data
        )

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == StatusCodes.SUCCESS.code)
        Assert.assertTrue(r1.getOrElse { "" } == "updated 1")
    }


    fun ensure(action:String, verb:String, args:Map<String,Any>, namer: Namer?, callback:(Result<*, *>) -> Unit): Unit {

        val apis = ApiContainer(ctx, apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")), auth = null, allowIO = false,  middleware = listOf(Restify()))
        val r1 = apis.call("app", "SampleREST", action, verb, mapOf(), args)
        callback(r1)

        val api2 = ApiContainer(ctx, apis = listOf(Api(SampleRESTApi::class, "app", "SampleREST")), auth = null, allowIO = false, middleware = listOf(Restify()), namer = namer)
        val name = namer?.rename("SampleREST")  ?: "SampleREST"
        val act  = namer?.rename(action) ?: action
        val r2 = api2.call("app", name, act, verb, mapOf(), args)
        callback(r2)
    }
}
