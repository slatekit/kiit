/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.core.cache.SimpleCache
import slatekit.core.cache.CacheSettings


//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>


class Example_Cache  : Command("auth") {

  //<doc:setup>
  data class Movie(val title:String)

  val cache = SimpleCache(CacheSettings(10) )
  //</doc:setup>

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    // CASE 1: Put a non-expiring item in the cache
    // NOTE: This loads the data into the cache using the function ( fetchers param )
    // supplied. The loading is done asynchronously in a future.
    cache.put("recent-movies", "recent movies", 300, { ->
      listOf(Movie("Arrival"), Movie("Lego Batman"))
    })

    // CASE 2: Put an item that expires in 300 seconds ( 5 minutes )
    cache.put("top", "top movies", 300, { ->
      listOf(Movie("Indiana Jones"), Movie("Batman"))
    })
    cache.put("trending", "trending movies", 300, { ->
      listOf(Movie("Dr. Strange"))
    })

    // CASE 3: Get the total number of items in cache
    println( cache.size())

    // CASE 4: Get one of the items in cache ( expects to be available and alive(not expired))
    val recent = cache.get<List<Movie>>("recent-movies")
    println( recent )

    // CASE 5: Get one of the items in cache if it exists and isAlive,
    // otherwise, loads/refreshes it again and gives you back a Future
    val result = cache.getOrLoad<List<Movie>>("recent-movies")
    println(result)

    // CASE 6: Get one of the items in cache via an explicit refresh
    val refresh = cache.getFresh<List<Movie>>("recent-movies")
    println(refresh)

    // CASE 7: Explicitly refresh one of the items ( without caring about getting notified )
    cache.refresh("recent-movies")

    // CASE 8: Invalidate a cache item by setting it to expired
    // NOTE: This will result in :
    // 1. get[T](key) calls returning null but triggering a refresh for that item
    // 2. getOrLoad[T](key) calls calling refresh
    cache.invalidate("recent-movies")

    // CASE 9: Invalidates all the cache items setting their time to expired.
    // NOTE: This will result in :
    // 1. get[T](key) calls returning null but triggering a refresh for that item
    // 2. getOrLoad[T](key) calls calling refresh
    cache.invalidateAll()

    // CASE 10: Removes the cache item completely.
    // NOTE: This will not be accessible anymore until you call .put again.
    cache.remove("top-movies")

    // CASE 11: Get the cache item entry ( contains data + metadata )
    val recentEntry = cache.getEntry("recent-movies")

    // Print info about the cache item
    // - key         : the name of the cache key
    // - text        : an optional description of the cache item
    // - seconds     : number of seconds to cache before it is invalidated
    // - expires     : the expiration time
    // - updated     : last time the item was refreshed/updated
    // - accessCount : number of times accessed
    // - accessed    : last time it was accessed
    // - error       : last exception when refreshing
    // - errorCount  : total number of exceptions
    // NOTE: just using ".get" here for example purposes
    println( recentEntry?.key         )
    println( recentEntry?.text        )
    println( recentEntry?.seconds     )
    println( recentEntry?.expires     )
    println( recentEntry?.updated     )
    println( recentEntry?.accessCount )
    println( recentEntry?.accessed    )
    println( recentEntry?.error       )
    println( recentEntry?.errorCount  )
    println( recent )
    //</doc:examples>

    return Success("")
  }
}
