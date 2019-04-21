package slatekit.info

import slatekit.entities.Entities
import slatekit.entities.EntityRepo
import slatekit.entities.EntityService
import slatekit.results.Outcome
import slatekit.results.Success

class DependencyService(entities: Entities, repo: EntityRepo<Long, Dependency>)
    : EntityService<Long, Dependency>(repo) {


    fun seed():Outcome<List<String>>{
        val version = "0.9.15"
        val items = listOf(
                Dependency(0L, "slatekit-results", "slatekit-results", "Model successes and failures with optional status codes"  , true, version, "slatekit.results", "slatekit-results.jar","slatekit.results"),
                Dependency(0L, "slatekit-common" , "slatekit-common" , "Common utilities for both client and server side code"    , true, version, "slatekit.common" , "slatekit-common.jar" ,"slatekit.results"),
                Dependency(0L, "slatekit-app"    , "slatekit-app"    , "Application template with several built-in features"      , true, version, "slatekit.app"    , "slatekit-app.jar"    ,"slatekit.results"),
                Dependency(0L, "slatekit-cli"    , "slatekit-cli"    , "Command Line Interface template with built-in features"   , true, version, "slatekit.cli"    , "slatekit-cli.jar"    ,"slatekit.results"),
                Dependency(0L, "slatekit-db"     , "slatekit-db"     , "Database utilities and functional wrappers on JDBC"       , true, version, "slatekit.db"     , "slatekit-db.jar"     ,"slatekit.results"),
                Dependency(0L, "slatekit-meta"   , "slatekit-meta"   , "Reflection and metadata related utility code"             , true, version, "slatekit.meta"   , "slatekit-meta.jar"   ,"slatekit.results"),
                Dependency(0L, "slatekit-query"  , "slatekit-query"  , "Query builder to safely build simple/moderate sql queries", true, version, "slatekit.query"  , "slatekit-query.jar"  ,"slatekit.results")
        )
        val results = items
                .map { it.withId( create(it) )}
                .map { "created : id=${it.id}, name=${it.name}, desc=${it.desc}" }
        return Success(results)
    }
}