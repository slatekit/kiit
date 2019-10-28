package slatekit.apis.setup

import slatekit.apis.ApiServer

interface HostAware {
    fun setApiHost(host: ApiServer)
}
