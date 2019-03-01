package slatekit.integration.errors

import slatekit.apis.*
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.Ignore
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext
import slatekit.results.Notice

@Api(area = "app", name = "errors", desc = "messaging service for users and groups", auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class ErrorItemApi(entCtx: AppEntContext)
    : ApiBaseEntity<Long, ErrorItem, ErrorItemService>(entCtx, Long::class, ErrorItem::class), ApiHostAware {

    private var container: ApiContainer? = null

    @Ignore
    override fun setApiHost(host: ApiContainer) {
        container = host
        service.setApiHost(host)
    }

    @ApiAction(desc = "retries an item using its id")
    fun retryById(id: Long, deleteOnSuccess: Boolean): Notice<String> {
        return service.retryById(id, deleteOnSuccess)
    }

    @ApiAction(desc = "retries the last item")
    fun retryLast(deleteOnSuccess: Boolean): Notice<String> {
        return service.retryLast(deleteOnSuccess)
    }

    @ApiAction(desc = "retries the recent x number of items")
    fun retryRecent(count: Int, deleteOnSuccess: Boolean): Notice<String> {
        return service.retryRecent(count, deleteOnSuccess)
    }

    @ApiAction(desc = "retries all items using a background worker")
    fun retryByWorker(deleteOnSuccess: Boolean): Notice<String> {
        return service.retryByWorker(deleteOnSuccess)
    }
}
