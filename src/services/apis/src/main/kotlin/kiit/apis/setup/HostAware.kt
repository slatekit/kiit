package kiit.apis.setup

import kiit.apis.ApiServer

interface HostAware {
    fun setApiHost(host: ApiServer)
}
