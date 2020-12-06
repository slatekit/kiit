package slatekit.examples

import kotlinx.coroutines.runBlocking
import slatekit.apis.ApiServer
import slatekit.apis.AuthMode
import slatekit.apis.Verb
import slatekit.apis.core.Api
import slatekit.common.conf.Config
import slatekit.results.Try
import slatekit.results.Success


import slatekit.examples.common.*
import slatekit.integration.common.AppEntContext


/**
 * Created by kreddy on 3/15/2016.
 */
class Guide_APIs : Command("types") {

    override fun execute(request: CommandRequest): Try<Any> {

        return Success("")
    }


    fun setup() {

        val ctx = AppEntContext.sample(Config(), "myapp", "myapp", "App1", "Company1")
        val api = Api(MovieApi(ctx), "manage", "movies", "", auth = AuthMode.None)
        val container = ApiServer(
                ctx  = ctx,
                apis = listOf(
                        Api(MovieApi(ctx), declaredOnly = false)
                )
        )
        runBlocking {
            container.call("app", "movies", "createSample", Verb.Post,
                    opts = mapOf(),
                    args = mapOf()
            )
        }
    }


    fun printAll(tag: String, models: List<Movie>): Unit {
        println()
        println(tag.toUpperCase())
        for (model in models)
            printOne(null, model)
    }


    fun printOne(tag: String?, model: Movie?): Unit {
        tag?.let { t ->
            println()
            println(t.toUpperCase())
        }

        model?.let { m ->
            println("User: " + m.id + ", " + m.title + ", " + m.category)
        }
    }
}
