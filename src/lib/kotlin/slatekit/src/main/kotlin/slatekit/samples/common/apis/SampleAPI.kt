package slatekit.samples.common.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.Verbs
import slatekit.apis.support.ApiBase
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.requests.Request
import slatekit.common.Context
import slatekit.common.Sources
import slatekit.common.auth.Roles
import slatekit.common.info.About
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.samples.common.models.SampleMovie


@Api(area = "samples", name = "types", desc = "sample to test features of Slate Kit APIs", auth = "", verb = Verbs.Auto, protocols = [Sources.Web])
class SampleApi(context: Context) : ApiBase(context) {

    var inc = 0


    @Action(desc = "info about this api")
    fun about(): About {
        return context.app
    }


    @Action(desc = "accepts supplied basic data types from send")
    fun hello(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "increments a simple counter")
    fun increment(): Int {
        inc += 1
        return inc
    }


    @Action(desc = "get current value of counter")
    fun getCounter(): Int {
        return inc
    }


    @Action(desc = "test post")
    fun create1(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test put")
    fun update1(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test post")
    fun process1(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test delete")
    fun delete1(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test patch")
    fun patch(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "test access to send")
    fun request(request: Request, greeting: String): String {
        val greetFromBody = request.data.getString("greeting")
        return "auto mapped: $greeting, manual get from body: $greetFromBody"
    }


    @Action(desc = "test wrapped result")
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


    @Action(desc = "test movie list")
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


    @Action(desc = "accepts supplied basic data types from send")
    fun inputBasicTypes(string1: String, bool1: Boolean, numShort: Short, numInt: Int, numLong: Long, numFloat: Float, numDouble: Double, date: DateTime): String {
        return "$string1, $bool1, $numShort $numInt, $numLong, $numFloat, $numDouble, $date"
    }
}