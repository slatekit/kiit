---
layout: start_page_mods_infra
title: module Cache
permalink: /kotlin-mod-cache
---

# Cache

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Light-weight cache to load, store, and refresh data, with support for metrics and time-stamps. Default in-memory implementation available | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.core.jar  |
| **namespace** | slatekit.core.cache  |
| **source core** | slatekit.core.cache.Cache.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Cache.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Cache.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.core.cache.Cache
import slatekit.core.cache.CacheSettings




// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin


  data class Movie(val title:String)

  val cache = Cache(CacheSettings(10) )
  

```

## Usage
```kotlin


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
    val recentEntry = cache.getCacheItem("recent-movies")

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
    

```

