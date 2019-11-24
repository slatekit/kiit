package slatekit.examples

import kotlinx.coroutines.runBlocking
import slatekit.results.Try
import slatekit.results.Success
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.cache.*
import slatekit.common.ext.toNumeric
import slatekit.common.types.Countries
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
        val raw = SimpleCache(CacheSettings(10))
        val cache = SyncCache(raw)
        //</doc:setup>

        // Write
        cache.put("countries", "supported countries", 300) {
            listOf(Countries.usa, Countries.find("GB"))
        }

        cache.put("promos", "promotion codes", 300) {
            listOf("p1", "p2")
        }

        val c1 = cache.get<List<Country>>("countries")
        val c2 = cache.get<List<String>>("promos")

        println(c1)
        println(c2)

        cache.stats().forEach {
            println("key    : " + it.key)
            println("expiry : " + "started=" + it.expiry.started.toNumeric() + ", seconds=" + it.expiry.seconds + ", expires=" + it.expiry.expires.toNumeric())
            println("reads  : " + "count=" + it.reads.count + ", timestamp=" + it.reads.timestamp?.toNumeric() )
            println("value  : " + "created=" + it.value.created?.toNumeric() + ", updated=" + it.value.updated?.toNumeric() + ", count=" + it.value.count)
            println("error  : " + "created=" + it.error.created?.toNumeric() + ", updated=" + it.error.updated?.toNumeric() + ", count=" + it.error.count)
            println("\n")
        }
        // Allows for override
        cache.set("promos", listOf("p1", "p2"))
        val c2b = cache.get<List<String>>("promos")
        println(c2b)
    }
}
