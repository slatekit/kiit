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


@Api(area = "samples", name = "all", desc = "sample to test features of Slate Kit APIs",
        auth = AuthModes.NONE, verb = Verbs.AUTO, sources = [Sources.ALL])
class SampleAPI(context: Context) : ApiBase(context) {

    // Simple value to test actions/methods
    private var accumulator = 0


    @Action(desc = "info about this api")
    fun about(): About {
        return context.info.about
    }


    @Action(desc = "accepts supplied basic data types from send")
    fun greet(greeting: String): String {
        return "$greeting back"
    }


    @Action(desc = "increments a simple accumulator")
    fun inc(): Int {
        accumulator += 1
        return accumulator
    }


    @Action(desc = "get current value of counter")
    fun value(): Int {
        return accumulator
    }


    @Action(desc = "add a value to a simple accumulator")
    fun add(value:Int): Int {
        accumulator += value
        return accumulator
    }


    /**
     * Make this only available on the CLI ( demo )
     */
    @Action(desc = "subtracts a value to a simple accumulator", sources = [Sources.CLI])
    fun sub(value:Int): Int {
        accumulator += value
        return accumulator
    }


    @Action(desc = "accepts supplied basic data types from send")
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


    @Action(desc = "get lists of movies")
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


    @Action(desc = "test access to send")
    fun request(request: Request, greeting: String): String {
        val greetFromBody = request.data.getString("greeting")
        return "Handled Request: got `$greeting` as parameter, got `$greetFromBody` from request body"
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


    @Action(desc = "test post", sources = [Sources.API])
    fun create(movie: SampleMovie): String {
        return "movie ${movie.title} created"
    }


    @Action(desc = "test put", sources = [Sources.API])
    fun update(movie: SampleMovie): String {
        return "movie ${movie.title} updated"
    }


    @Action(desc = "test patch", sources = [Sources.API])
    fun patch(id:Long, fields: List<Patch>): String {
        val info = fields.joinToString("") { i -> i.name + "=" + i.value }
        return "movie ${id} updated with $info"
    }


    @Action(desc = "test delete", sources = [Sources.API])
    fun delete(movie: SampleMovie): String {
        return "movie ${movie.title} deleted"
    }
}


