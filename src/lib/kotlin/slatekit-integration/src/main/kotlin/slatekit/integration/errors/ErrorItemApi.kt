package slatekit.integration.errors

import slatekit.apis.*
import slatekit.apis.support.ApiBase
import slatekit.common.Request
import slatekit.common.ResultMsg
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext


@Api(area = "app", name = "errors", desc= "messaging service for users and groups"
        , roles= "admin", auth = "app-roles", verb = "*", protocol = ApiConstants.SourceCLI)
class ErrorItemApi(entCtx: AppEntContext )
    : ApiBaseEntity<ErrorItem, ErrorItemService>(entCtx, ErrorItem::class), ApiHostAware {


    private var container: ApiContainer? = null
    override fun setApiHost(host: ApiContainer) {
        container = host
        service.setApiHost(host)
    }

    @ApiAction(name = "", desc = "retries an item using its id", roles = "@parent", protocol = "cli")
    fun retryById(id:Long, deleteOnSuccess:Boolean) : ResultMsg<String> {
        return service.retryById(id, deleteOnSuccess)
    }


    @ApiAction(name = "", desc = "retries the last item", roles = "@parent", protocol = "cli")
    fun retryLast(deleteOnSuccess:Boolean) : ResultMsg<String> {
        return service.retryLast(deleteOnSuccess)
    }


    @ApiAction(name = "", desc = "retries the recent x number of items", roles = "@parent", protocol = "cli")
    fun retryRecent(count:Int, deleteOnSuccess:Boolean) : ResultMsg<String> {
        return service.retryRecent(count, deleteOnSuccess)
    }


    @ApiAction(name = "", desc = "retries all items using a background worker", roles = "@parent", protocol = "cli")
    fun retryByWorker(deleteOnSuccess:Boolean) : ResultMsg<String> {
        return service.retryByWorker(deleteOnSuccess)
    }
}
