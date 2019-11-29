---
title: "Cache"
date: 2019-11-17T23:55:41-05:00
section_header: Cache
---

# Overview
The Slate Cache is a light-weight LRU ( Least Recently Used ) Cache for both Android and Server. While there are many comprehensive Cache solutions available for the JVM, this is designed to be an **in-memory light-weight** cache with an emphasis on **diagnostics** and **async** functionality via **coroutines and channels**.
{{% break %}}

# Goals
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Light-Weight</strong></td>
        <td>Simple, light-weight with default implementations for sync and async based Caches.</td>
    </tr>
    <tr>
        <td><strong>2. Diagnostics </strong> </td>
        <td>Provides a reasonable level of diagnostics and cache metrics</td>                     
    </tr>
    <tr>
        <td><strong>3. Coroutines</strong></td>
        <td>Async based cache leverages Coroutines / Channels for write operations</td>
    </tr>
</table>
{{% break %}}

# Index
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong><a class="url-ch" href="arch/cache#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
</table>

{{% section-end mod="arch/cache" %}}

# Status
This component is currently stable. Future versions will include support for:
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Feature</strong></td>
        <td><strong>Status</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>Events</strong></td>
        <td>Upcoming</td>
        <td>An event emitter for changes to cache</td>
    </tr>
    <tr>
        <td><strong>Refresh</strong></td>
        <td>Upcoming</td>
        <td>Automatic / scheduled refresh of cache items</td>
    </tr>
    <tr>
        <td><strong>Stats</strong></td>
        <td>Upcoming</td>
        <td>Enhanced stats to capture evictions/clearing of cache</td>
    </tr>
</table>
{{% break %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-cache:1.0.0'
    }

{{< /highlight >}}
{{% sk-module 
    name="Cache"
    package="slatekit.cache"
    jar="slatekit.cache.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-cache"
    gitAlias="slatekit/src/lib/kotlin/slatekit-cache"
    url="arch/cache"
    uses="slatekit.results, slatekit.common"
    exampleUrl=""
    exampleFileName="Example_Cache.kt"
%}}
{{% section-end mod="arch/cache" %}}

# Requires
This component uses the following other <strong>Slate Kit</strong> and/or third-party components.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><a class="url-ch" href="core/results">Slate Kit - Results</a></td>
        <td>To model successes and failures with optional status codes</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/overview">Slate Kit - Common</a></td>
        <td>Common utilities for both android + server</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="arch/tracking">Slate Kit - Tracking</a></td>
        <td>Tracking and diagnostics</td>
    </tr>
</table>
{{% section-end mod="arch/cache" %}}

# Sample
{{< highlight kotlin >}}
    
    import slatekit.cache.*

    // SimpleCache is the underlying Cache implementation
    val raw:Cache = SimpleCache(CacheSettings(10))

    // Synchronized cache wraps the raw cache
    // NOTE: Async cache via coroutines/channels avialable (see docs below)
    val cache:SyncCache = SimpleSyncCache(raw)

    // Writes
    // 1. Put new entry ( using a function to fetch )
    cache.put("promos", "promotion codes", 300) { listOf("p1", "p2") }

    // 2. Force a refresh
    cache.refresh("promos")

    // Reads
    // 1. Get existing cache item
    val c1 = cache.get<List<Country>>("countries")

    // 2. Get existing cache item or load it if expired
    val c2 = cache.getOrLoad<List<String>>("promos")

    // Stats
    cache.stats().forEach {
        println("key    : " + it.key)
        
        println("expiry.start   : " + it.expiry.started.toStringUtc())
        println("expiry.seconds : " + it.expiry.seconds )
        println("expiry.expires : " + it.expiry.expires.toStringUtc())
        
        println("hits.count     : " + it.hits.count)
        println("hits.time      : " + it.hits.timestamp?.toStringUtc() )
        
        println("value.created  : " + it.value.created?.toStringUtc() )
        println("value.updated  : " + it.value.updated?.toStringUtc() )
        println("value.applied  : " + it.value.applied)

        println("error.created  : " + it.error.created?.toStringUtc() )
        println("error.updated  : " + it.error.updated?.toStringUtc() )
        println("error.applied  : " + it.error.applied)
        println("\n")
    }

{{< /highlight >}}
{{% section-end mod="arch/cache" %}}


# Features
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Usage</strong></td>
        <td>Usage of the features</td>
        <td><a href="arch/cache/#usage" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Stats</strong> </td>
        <td>Gettign stats and cache diagnostics</td> 
        <td><a href="arch/cache/#stats" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Sync</strong></td>
        <td>How to convert raw text into parsed parameters</td>
        <td><a href="arch/cache/#sync" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Async</strong></td>
        <td>Working with parsed commands as CLI Requests</td>
        <td><a href="arch/cache/#asyc" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>

{{% section-end mod="arch/cache" %}}


## Usage {#usage}
Showing simple usage of a **synchronous** cache implementation all operations on cache are using the **@synchronized** annotation on methods.

{{< highlight kotlin >}}

    import slatekit.cache.*

    // SimpleCache is the underlying Cache implementation
    val raw:Cache = SimpleCache(CacheSettings(10))

    // Synchronized cache wraps the raw cache
    // NOTE: Async cache via coroutines/channels avialable (see docs below)
    val cache:SyncCache = SimpleSyncCache(raw)

    // Writes: Both sync and async versions have these put/set/refresh methods
    // 1. Put new entry ( using a function to fetch )
    cache.put("promos", "promotion codes", 300) { listOf("p1", "p2") }

    // 2. Update existing entry with value
    cache.set("promos", listOf("p1", "p2"))

    // 3. Force a refresh
    cache.refresh("promos")

    // Reads: 
    // NOTES:
    // 1. The sync version returns the value
    // 2. The async version returns a Deferred<T> 
    val c1 = cache.get<List<Country>>("countries")

    // 2. Get existing cache item or load it if expired
    val c2 = cache.getOrLoad<List<String>>("promos")

    // 3. Get after refreshing it first
    val c3 = cache.getFresh<List<String>>("promos")

    println(c1)
    println(c2)
    println(c3)

{{< /highlight >}}

{{% feature-end mod="arch/cache" %}}

## Stats {#stats}
The main difference in the stats for this component is that both the **created, updated** timestamps are maintained while also typically keeping track of the counts of **accesses, hits, misses**.

<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Term</strong></td>
        <td><strong>Desc</strong></td>
        <td><strong>Example</strong></td>
        <td><strong>Ratio</strong></td>
    </tr>
    <tr>
        <td><strong>Accesses</strong></td>
        <td>Number of times a cache item is accessed</td>
        <td>100</td>
        <td>n/a</td>
    </tr>
    <tr>
        <td><strong>Hits</strong></td>
        <td>Number of times a cache item is accessed and it exists</td>
        <td>80</td>
        <td>.8</td>
    </tr>
    <tr>
        <td><strong>Misses</strong></td>
        <td>Number of times a cache item is accessed but does not exist</td>
        <td>20</td>
        <td>.2</td>
    </tr>
</table>

{{< highlight kotlin >}}
    
    // ... setup ( see above )
    cache.stats().forEach {
        println("key    : " + it.key)

        println("expiry.start   : " + it.expiry.started.toStringUtc())
        println("expiry.seconds : " + it.expiry.seconds )
        println("expiry.expires : " + it.expiry.expires.toStringUtc())

        println("access.count     : " + it.reads?.count)
        println("access.time      : " + it.reads?.timestamp?.toStringUtc() )

        println("hits.count     : " + it.hits.count)
        println("hits.time      : " + it.hits.timestamp?.toStringUtc() )

        println("misses.count     : " + it.misses?.count)
        println("misses.time      : " + it.misses?.timestamp?.toStringUtc() )

        println("value.created  : " + it.value.created?.toStringUtc() )
        println("value.updated  : " + it.value.updated?.toStringUtc() )
        println("value.applied  : " + it.value.applied)

        println("error.created  : " + it.error.created?.toStringUtc() )
        println("error.updated  : " + it.error.updated?.toStringUtc() )
        println("error.applied  : " + it.error.applied)
        println("\n")
    }

{{< /highlight >}}
{{% feature-end mod="arch/cache" %}}

## Sync {#sync}
Showing usage of a **synchronous** cache implementation {{% sk-link-code component="cache" filepath="cache/SimpleAsyncCache.kt" name="SimpleAsyncCache" %}}. All operations on cache are using the **@synchronized** annotation on methods. Synchronouse caches are implemented using the {{% sk-link-code component="cache" filepath="cache/SyncCache.kt" name="SyncCache" %}} interface.
{{< highlight kotlin >}}

    import slatekit.cache.*

    // SimpleCache is the underlying Cache implementation
    val raw:Cache = SimpleCache(CacheSettings(10))

    // Synchronized cache wraps the raw cache
    // NOTE: Async cache via coroutines/channels avialable (see docs below)
    val cache:SyncCache = SimpleSyncCache(raw)

    // Writes
    // 1. Put new entry ( using a function to fetch )
    cache.put("promos", "promotion codes", 300) { listOf("p1", "p2") }

    // 2. Update existing entry with value
    cache.set("promos", listOf("p1", "p2"))

    // 3. Force a refresh
    cache.refresh("promos")

    // Reads
    // 1. Get existing cache item
    val c1 = cache.get<List<Country>>("countries")

    // 2. Get existing cache item or load it if expired
    val c2 = cache.getOrLoad<List<String>>("promos")

    // 3. Get after refreshing it first
    val c3 = cache.getFresh<List<String>>("promos")

    println(c1)
    println(c2)
    println(c3)

{{< /highlight >}}
{{% feature-end mod="arch/cache" %}}

## Async {#async}
Async functionality is based on using Coroutines and Channels for writes.
The **async** cache implementation is {{% sk-link-code component="cache" filepath="cache/SimpleAsyncCache.kt" name="SimpleAsyncCache" %}}. Async caches are implemented using the {{% sk-link-code component="cache" filepath="cache/AsyncCache.kt" name="AsyncCache" %}} interface. In this approach, concurrent writes are handling by creating a {{% sk-link-code component="cache" filepath="cache/CacheCommand.kt" name="CacheCommand" %}} representing the operation and sending them to a channel to be handled sequentially. Reads are handling by returning a kotlin **Deferred[T]**. 
{{< highlight kotlin >}}
     
    import slatekit.cache.*

    val logger = LoggerConsole()
    val coordinator = ChannelCoordinator(logger, Paired(), Channel<CacheCommand>(Channel.UNLIMITED))
    val asyncCache: AsyncCache = SimpleAsyncCache(raw, coordinator)

    // Writes
    // 1. Put new entry ( using a function to fetch )
    asyncCache.put("promos", "promotion codes", 300) { listOf("p1", "p2") }

    // 2. Update existing entry with value
    asyncCache.set("promos", listOf("p1", "p2"))

    // 3. Force a refresh
    asyncCache.refresh("promos")

    // Reads
    // 1. Get existing cache item
    val a1 = asyncCache.get<List<Country>>("countries").await()

    // 2. Get existing cache item or load it if expired
    val a2 = asyncCache.getOrLoad<List<String>>("promos").await()

    // 3. Get after refreshing it first
    val a3 = asyncCache.getFresh<List<String>>("promos").await()
     

{{< /highlight >}}
{{% feature-end mod="arch/cache" %}}

{{% section-end mod="arch/cache" %}}

