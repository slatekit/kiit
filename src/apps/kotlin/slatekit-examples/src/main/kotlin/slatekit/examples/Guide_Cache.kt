package slatekit.examples

import kotlinx.coroutines.runBlocking
import slatekit.results.Try
import slatekit.results.Success


import slatekit.cache.*
import slatekit.common.Identity
import slatekit.common.ext.toStringUtc
import slatekit.common.log.LoggerConsole
import slatekit.common.types.Country


/**
 * Created by kreddy on 3/15/2016.
 */
class Guide_Cache : Command("types") {

    override fun execute(request: CommandRequest): Try<Any> {

        sync()
        return Success("")
    }


    fun sync() {

        //<doc:section name="sync">
        val id = Identity.app("app", "cache")
        val raw = SimpleCache(id, settings = CacheSettings(10))
        val syncCache:Cache = SimpleSyncCache(raw)
        //</doc:setup>

        // Writes
        // 1. Put new entry ( using a function to fetch )
        syncCache.put("promos", "promotion codes", 300) { listOf("p1", "p2") }

        // 2. Update existing entry with value
        syncCache.set("promos", listOf("p1", "p2"))

        // 3. Force a refresh
        syncCache.refresh("promos")

        // Reads
        // 1. Get existing cache item
        val c1 = syncCache.get<List<Country>>("countries")

        // 2. Get existing cache item or load it if expired
        val c2 = syncCache.getOrLoad<List<String>>("promos")

        // 3. Get after refreshing it first
        val c3 = syncCache.getFresh<List<String>>("promos")

        println(c1)
        println(c2)
        println(c3)

        syncCache.stats().forEach {
            println("key    : " + it.key)

            println("expiry.start   : " + it.expiry.started.toStringUtc())
            println("expiry.seconds : " + it.expiry.seconds )
            println("expiry.expires : " + it.expiry.expires.toStringUtc())

            println("access.count     : " + it.reads?.count)
            println("access.time      : " + it.reads?.updated?.toStringUtc() )

            println("hits.count     : " + it.hits.count)
            println("hits.time      : " + it.hits.updated?.toStringUtc() )

            println("misses.count     : " + it.misses?.count)
            println("misses.time      : " + it.misses?.updated?.toStringUtc() )

            println("value.created  : " + it.value.created?.toStringUtc() )
            println("value.updated  : " + it.value.updated?.toStringUtc() )
            println("value.applied  : " + it.value.count)

            println("error.created  : " + it.error.created?.toStringUtc() )
            println("error.updated  : " + it.error.updated?.toStringUtc() )
            println("error.applied  : " + it.error.count)
            println("\n")
        }


        // Async
        runBlocking {
            val logger = LoggerConsole()
            val asyncCache: AsyncCache = SimpleAsyncCache.of(id, logger = logger)

            // Writes
            // 1. Put new entry ( using a function to fetch )
            asyncCache.put("promos", "promotion codes", 300) { listOf("p1", "p2") }

            // 2. Update existing entry with value
            asyncCache.set("promos", listOf("p1", "p2"))

            // 3. Force a refresh
            asyncCache.refresh("promos")

            // Reads
            // 1. Get existing cache item
            val a1 = asyncCache.getAsync<List<Country>>("countries").await()

            // 2. Get existing cache item or load it if expired
            val a2 = asyncCache.getOrLoadAsync<List<String>>("promos").await()

            // 3. Get after refreshing it first
            val a3 = asyncCache.getFreshAsync<List<String>>("promos").await()
        }
        println("done")
    }
}
