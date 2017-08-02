package slatekit.examples

import slatekit.apis.ApiContainer
import slatekit.apis.ApiReg
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import slatekit.core.common.AppContext
import slatekit.examples.common.*


/**
 * Created by kreddy on 3/15/2016.
 */
class Guide_APIs : Cmd("types") {

    override fun executeInternal(args: Array<String>?): Result<Any> {

        return ok()
    }


    fun setup():Unit {

        val ctx = AppContext.simple("myapp")
        val container = ApiContainer(
                ctx  = ctx,
                auth = null,
                apis = listOf(
                        ApiReg(MovieApi(ctx), declaredOnly = false)
                ),
                allowIO = false
        )
        container.call("app", "movies", "createSample", "post",
                opts = mapOf(),
                args = mapOf()
        )
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
