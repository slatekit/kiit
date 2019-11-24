package slatekit.cache

import slatekit.core.common.Coordinator

/**
 * Provides coordinated ( default approach = Channel ) access to cache operations
 * Read operations can be either optimistic and or callback based.
 */
class AsyncCache(val cache:Cache, private val coordinator: Coordinator<CacheCommand>) : Management {


    override suspend fun request(command: CacheCommand) {
        coordinator.send(command)
    }


    /**
     * Listens to incoming requests ( name of worker )
     */
    suspend fun manage() {
        coordinator.consume { request ->
            manage(request, false)
        }
    }


    private suspend fun manage(cmd:CacheCommand, launch:Boolean) {
        when(cmd) {
            is CacheCommand.ClearAll -> { cache.clear() }
            is CacheCommand.Clear    -> { cache.remove(cmd.key) }
            is CacheCommand.Check    -> { cache.clear() }
            is CacheCommand.Del      -> { cache.remove(cmd.key) }
            is CacheCommand.Refresh  -> { cache.refresh(cmd.key) }
            is CacheCommand.Put      -> { cache.put(cmd.key, cmd.desc, cmd.expiryInSeconds, cmd.fetcher) }
            is CacheCommand.Set      -> { cache.set(cmd.key, cmd.value) }
            is CacheCommand.Get      -> { cmd.onReady(cache.get(cmd.key)) }
        }
    }
}
