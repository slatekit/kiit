---
layout: start_page_mods_infra
title: module Cache
permalink: /mod-cache
---

# Cache

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Light-weight cache to load, store, and refresh data, with support for metrics and time-stamps. Default in-memory implementation available | 
| **date**| 2017-04-12T22:59:15.661 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.cache  |
| **source core** | slate.core.cache.Cache.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/cache](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/cache)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Cache.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Cache.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.Result
import slate.common.results.ResultFuncs._
import slate.core.cache._
import scala.util.{Success, Failure}


// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala


  case class Movie(title:String)

  val ctx = scala.concurrent.ExecutionContext.global
  val cache = new Cache(new CacheSettings(10), ctx )
  

```

## Usage
```scala


    // CASE 1: Put a new non-expiring item in the cache
    // NOTE: This loads the data into the cache using the function ( fetchers param )
    // supplied. The loading is done asynchronously in a future.
    cache.put("recent-movies", Some("recent movies"), None, () => {
      Option(List[Movie](Movie("Arrival"), Movie("Lego Batman")))
    })

    // CASE 2: Put an item that expires in 300 seconds ( 5 minutes )
    cache.put("top", Some("top movies"), Some(300), () => {
      Option(List[Movie](Movie("Indiana Jones"), Movie("Batman")))
    })
    cache.put("trending", Some("trending movies"), Some(300), () => {
      Option(List[Movie](Movie("Dr. Strange")))
    })

    // CASE 3: Get the total number of items in cache
    println( cache.size())

    // CASE 4: Get one of the items in cache ( expects to be available and alive(not expired))
    val recent = cache.get[List[Movie]]("recent-movies")
    println( recent )

    // CASE 5: Get one of the items in cache if it exists and isAlive,
    // otherwise, loads/refreshes it again and gives you back a Future
    val result = cache.getOrLoad[List[Movie]]("recent-movies")
    result.onComplete {
      case Success(s) => println(s)
      case Failure(e) => println(e.getMessage)
    }(ctx)

    // CASE 6: Get one of the items in cache via an explicit refresh
    val refresh = cache.getFresh[List[Movie]]("recent-movies")
    refresh.onComplete {
      case Success(s) => println(s)
      case Failure(e) => println(e.getMessage)
    }(ctx)

    // CASE 7: Explicitly refresh one of the items ( without caring about getting notified )
    cache.refresh("recent-movies")

    // CASE 8: Invalidate a cache item by setting it to expired
    // NOTE: This will result in :
    // 1. get[T](key) calls returning None but triggering a refresh for that item
    // 2. getOrLoad[T](key) calls calling refresh
    cache.invalidate("recent-movies")

    // CASE 9: Invalidates all the cache items setting their time to expired.
    // NOTE: This will result in :
    // 1. get[T](key) calls returning None but triggering a refresh for that item
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
    println( recentEntry.get.key         )
    println( recentEntry.get.text        )
    println( recentEntry.get.seconds     )
    println( recentEntry.get.expires     )
    println( recentEntry.get.updated     )
    println( recentEntry.get.accessCount )
    println( recentEntry.get.accessed    )
    println( recentEntry.get.error       )
    println( recentEntry.get.errorCount  )
    println( recent )
    

```

