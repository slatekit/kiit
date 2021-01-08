package slatekit.samples.common.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.ApiBase
import slatekit.apis.core.Patch
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.requests.Request
import slatekit.context.Context
import slatekit.common.Sources
import slatekit.common.ext.toId
import slatekit.common.ext.toStringUtc
import slatekit.common.info.About
import slatekit.common.types.Doc
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.samples.common.models.SampleMovie


/**
 * Sample API that can be used on both
 * 1. CLI : Command line interface apps
 * 2. WEB : As Http/Web API ( see postman script at /doc/samples/apis/slatekit-samples-postman.json
 *
 * NOTES:
 * This has examples for showing
 * 1. Basic Types    : Basic data types ( bool, string, int, double, datetime )
 * 2. Complex Types  : Complex types such as classes/objects/lists/maps
 * 3. Requests       : Access to the request object
 * 4. Error handling : Working with errors and exceptions
 * 5. CLI Only       : Configuring actions for CLI access only
 * 6. WEB Only       : Configuring actions for WEB access only
 * 7. WEB Verbs      : HTTP Verbs post, put, patch, delete actions
 * 8. WEB Files      : File handling with uploads / downloads
 *
 */
@Api(area = "samples", name = "all", desc = "sample to test features of Slate Kit APIs", auth = AuthModes.NONE, verb = Verbs.AUTO, sources = [Sources.ALL])
class SampleAPI(context: Context) : ApiBase(context) {

    // Simple value to test actions/methods
    private var accumulator = 0


    /*
     Sample acton to show version, and other info about this app/api
     Examples:
     CLI: samples.all.about
     WEB: curl -X POST http://localhost:5000/api/samples/all/about
     */
    @Action(desc = "info about this api")
    fun about(): About {
        return context.info.about
    }


    /*
     Sample action to take in a input and return a simple response
     Examples:
     CLI: samples.all.greet -greeting="hey there"
     WEB: curl -X POST http://localhost:5000/api/samples/all/greet -d '{ "greeting": "hello" }'
     */
    @Action(desc = "simple hello world greeting")
    fun greet(greeting: String): String {
        return "$greeting back"
    }


    /*
     Sample action to increment the accumulator
     Examples:
     CLI: samples.all.inc
     WEB: curl -X POST http://localhost:5000/api/samples/all/inc
     */
    @Action(desc = "increments the accumulator")
    fun inc(): Int {
        accumulator += 1
        return accumulator
    }


    /*
     Simple action to add a value to the accumulator
     CLI: samples.all.add -value=2
     WEB: curl -X POST http://localhost:5000/api/samples/all/add -d '{ "value" : 2 }'
     */
    @Action(desc = "add a value to the accumulator")
    fun add(value:Int): Int {
        accumulator += value
        return accumulator
    }


    /*
     Sample action to get value of accumulator
     CLI: samples.all.value
     WEB: curl -X GET http://localhost:5000/api/samples/all/value
     */
    @Action(desc = "get current value of accumulator", verb = Verbs.GET)
    fun value(): Int {
        return accumulator
    }


    /*
     Sample action to show restricted access to only the CLI
     CLI: samples.all.sub -value=1
     WEB: Not available with this configuration
     */
    @Action(desc = "subtracts a value from the accumulator", sources = [Sources.CLI])
    fun sub(value:Int): Int {
        accumulator += value
        return accumulator
    }


    /*
     Sample action to show accepting different basic data types
     CLI: samples.all.inputs -name="jason" -isActive=true -age=32 -dept=2 -account=123 -average=2.4 -salary=120000 -date="2019-04-01T11:05:30Z"
     WEB: curl -X POST http://localhost:5000/api/samples/all/inputs \
        -H 'Content-Type: application/json' \
        -d '{
            "name"    : "kishore",
            "isActive"  : true,
            "age"     : 30,
            "dept"    : 10,
            "account" : 1234,
            "average" : 3.1,
            "salary"  : 100000,
            "date"    : "2019-04-01T11:05:30Z"
         }'
     */
    @Action(desc = "accepts supplied basic data types from send", verb = Verbs.GET)
    fun inputs(name: String, isActive: Boolean, age: Short, dept: Int, account: Long, average: Float, salary: Double, date: DateTime): Map<String, Any> {
        return mapOf(
                "name"    to name,
                "active"  to isActive,
                "age"     to age,
                "dept"    to dept,
                "account" to account,
                "average" to average,
                "salary"  to salary,
                "date"    to date.toStringUtc()
        )
    }


    /*
     Sample action to show retrieving complex objects
     CLI: samples.all.movies
     WEB: curl -X GET http://localhost:5000/api/samples/all/movies
     */
    @Action(desc = "get lists of movies", verb = Verbs.GET)
    fun movies(): List<SampleMovie> {
        return listOf(
                SampleMovie(
                        title = "Indiana Jones",
                        category = "action",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1985, 8, 10)
                ),
                SampleMovie(
                        title = "Contact",
                        category = "sci-fi",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1995, 8, 10)
                )
        )
    }


    /*
     Sample action ( similar to greeting above ), but with access to the Request object
     Examples:
     CLI: samples.all.request -greeting="hey there"
     WEB: curl -X POST http://localhost:5000/api/samples/all/request -d '{ "greeting": "hello" }'
     */
    @Action(desc = "test access to the request")
    fun request(request: Request, greeting: String): String {
        val greetFromBody = request.data.getString("greeting")
        return "Handled Request: got `$greeting` as parameter, got `$greetFromBody` from request body"
    }


    /*
     Sample action ( similar to greeting above ), to show error handling and returning statuses
     Examples:
     CLI: samples.all.response -status="invalid"
     WEB: curl -X POST http://localhost:5000/api/samples/all/request -d '{ "greeting": "hello" }'
     */
    @Action(desc = "test wrapped result")
    fun response(request: Request, status: String): Outcome<SampleMovie> {
        val sampleMovie = SampleMovie(
                title = "Sample Movie 1",
                category = "action",
                playing = false,
                cost = 10,
                rating = 4.5,
                released = DateTimes.of(1985, 8, 10)
        )
        val result:Outcome<SampleMovie> = when(status) {
            "invalid"  -> Outcomes.invalid ("test status invalid")
            "ignored"  -> Outcomes.ignored ("test status ignored")
            "denied"   -> Outcomes.denied  ("test status denied ")
            "errored"  -> Outcomes.errored ("test status errored")
            "conflict" -> Outcomes.conflict("test status conflict")
            "pending"  -> Outcomes.pending (sampleMovie)
            "success"  -> Outcomes.success (sampleMovie)
            else       -> Outcomes.unexpected("test status unknown")
        }
        return result
    }


    @Action(desc = "File upload", sources = [Sources.API])
    fun upload(file: Doc):Map<String, String> {
        return mapOf(
                "name" to file.name,
                "type" to file.tpe.http,
                "size" to file.size.toString(),
                "data" to file.content
        )
    }


    @Action(desc = "File download", sources = [Sources.API])
    fun download(text:String):Doc {
        return Doc.text(DateTime.now().toStringUtc().toId() + ".txt", text)
    }


    @Action(desc = "test movie list")
    fun recent(category: String): List<SampleMovie> {
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


    /**
     * The HTTP Verb handling is automatic or can be explicitly supplied.
     * Names starting with the following match to HTTP Verbs
     * 1. create -> post
     * 2. update -> put
     * 3. patch  -> patch
     * 4. delete -> delete
     */
    @Action(desc = "test post", sources = [Sources.API])
    fun create(movie: SampleMovie): String {
        return "movie ${movie.title} created"
    }


    /**
     * Example showing
     */
    @Action(desc = "test put", sources = [Sources.API])
    fun update(movie: SampleMovie): String {
        return "movie ${movie.title} updated"
    }


    @Action(desc = "test patch", sources = [Sources.API])
    fun patch(id:Long, fields: List<Patch>): String {
        val info = fields.joinToString("") { i -> i.name + "=" + i.value + " " }
        return "movie ${id} updated with $info"
    }


    @Action(desc = "test delete", sources = [Sources.API])
    fun delete(movie: SampleMovie): String {
        return "movie ${movie.title} deleted"
    }
}


