package slatekit.samples.server.apis

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiBase
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.requests.Request
import slatekit.common.Context
import slatekit.common.auth.Roles
import slatekit.common.db.IDb
import slatekit.common.info.About
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.samples.server.models.SampleMovie


@Api(area = "samples", name = "types", desc = "sample to test features of Slate Kit APIs",
        roles = Roles.none, auth = "", verb = Verbs.auto, protocol = Protocols.web)
class SampleApi(context: Context, val db:IDb) : ApiBase(context) {

    var inc = 0


    @ApiAction(desc = "info about this api")
    fun about(): About {
        return context.app
    }

    private var opened = false
    @ApiAction(desc = "info about this api")
    fun connect(id:Long): String {
        if(!opened) {
            db.open()
            opened = true
        }
        val name = db.getScalarString("select name from `test1` where id = $id", null)
        return name
    }


    @ApiAction(desc = "accepts supplied basic data types from request")
    fun hello(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "increments a simple counter")
    fun increment(): Int {
        inc += 1
        return inc
    }


    @ApiAction(desc = "get current value of counter")
    fun getCounter(): Int {
        return inc
    }


    @ApiAction(desc = "test post")
    fun create1(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "test put")
    fun update1(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "test post")
    fun process1(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "test delete")
    fun delete1(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "test patch")
    fun patch(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "test access to request")
    fun request(request: Request, greeting: String): String {
        val greetFromBody = request.data.getString("greeting")
        return "auto mapped: $greeting, manual get from body: $greetFromBody"
    }


    @ApiAction(desc = "test wrapped result")
    fun response(request: Request, category: String): Outcome<SampleMovie> {
        return Outcomes.success(
                SampleMovie(
                        title = "Sample Movie 1",
                        category = category,
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1985, 8, 10)
                ))
    }


    @ApiAction(desc = "test movie list")
    fun movies(category: String): List<SampleMovie> {
        return listOf(
                SampleMovie(
                        title = "Sample Movie 1",
                        category = category,
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1985, 8, 10)
                ),
                SampleMovie(
                        title = "Sample Movie 2",
                        category = category,
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1995, 8, 10)
                )
        )
    }


    @ApiAction(desc = "accepts supplied basic data types from request")
    fun inputBasicTypes(string1: String, bool1: Boolean, numShort: Short, numInt: Int, numLong: Long, numFloat: Float, numDouble: Double, date: DateTime): String {
        return "$string1, $bool1, $numShort $numInt, $numLong, $numFloat, $numDouble, $date"
    }
}