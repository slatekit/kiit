package test.cache

import slatekit.cache.CacheCommand
import slatekit.common.ids.Paired
import slatekit.common.log.Logger


open class MockCacheCoordinator() : slatekit.core.common.Coordinator<CacheCommand> {
    val requests = mutableListOf<CacheCommand>()

    override fun sendSync(cmd: CacheCommand) {
        requests.add(cmd)
    }

    override suspend fun send(req: CacheCommand) {
        requests.add(req)
    }

    override suspend fun poll(): CacheCommand? {
        val first = requests.firstOrNull()
        first?.let {
            requests.removeAt(0)
        }
        return first
    }

    override suspend fun consume(operation:suspend (CacheCommand) -> Unit ) {
        for(request in requests){
            operation(request)
        }
    }
}