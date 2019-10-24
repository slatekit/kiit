package slatekit.apis.security

import slatekit.apis.setup.Protocol

data class Protocols(val all:List<Protocol>) {
    companion object {
        val empty = Protocols(listOf())
        val all = Protocols(listOf(Protocol.All))
    }
}