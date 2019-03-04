package slatekit.server.sample

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiBase
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.requests.Request
import slatekit.core.common.AppContext
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs",
        roles = "", auth = "", verb = Verbs.auto, protocol = Protocols.web)
class SampleApi(context: AppContext) : ApiBase(context) {

    // For unit-tests
    var inc = 0


    @ApiAction(desc = "accepts supplied basic data types from request")
    fun hello(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "increments a simple counter")
    fun increment(): Int {
        inc += 1
        return inc
    }


    @ApiAction(desc = "increments a simple counter")
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


    @ApiAction(desc = "test patch")
    fun request(request: Request, greeting: String): String {
        val greetFromBody = request.data.getString("greeting")
        return "auto mapped: $greeting, manual get from body: $greetFromBody"
    }


    @ApiAction(desc = "test patch")
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


    @ApiAction(desc = "accepts supplied basic data types from request")
    fun inputBasicTypes(string1: String, bool1: Boolean, numShort: Short, numInt: Int, numLong: Long, numFloat: Float, numDouble: Double, date: DateTime): String {
        return "$string1, $bool1, $numShort $numInt, $numLong, $numFloat, $numDouble, $date"
    }
}