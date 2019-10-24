package slatekit.apis.setup

import slatekit.apis.ApiHost

interface HostAware {
    fun setApiHost(host: ApiHost)
}