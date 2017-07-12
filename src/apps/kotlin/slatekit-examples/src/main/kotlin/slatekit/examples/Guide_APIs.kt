package slatekit.examples

import slatekit.apis.ApiContainer
import slatekit.apis.ApiReg
import slatekit.apis.containers.ApiContainerCLI
import slatekit.common.DateTime
import slatekit.common.Model
import slatekit.common.Result
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.Db
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.db.DbLookup.DbLookupCompanion.namedDbs
import slatekit.common.db.DbTypeMySql
import slatekit.common.db.types.DbSourceMySql
import slatekit.common.mapper.Mapper
import slatekit.common.query.Query
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityInfo
import slatekit.entities.core.EntityMapper
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.EntityRepoMySql
import slatekit.examples.common.*
import slatekit.integration.AppApi
import slatekit.integration.VersionApi


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
                        ApiReg(MovieApi(ctx), false)
                )
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
