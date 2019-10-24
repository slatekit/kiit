package slatekit.apis.core

import slatekit.apis.setup.Protocol

data class Protocols(val all:List<Protocol>) {

    fun hasCLI(): Boolean {
        return all.any {  isCLI(it.name) }
    }


    companion object {
        val empty = Protocols(listOf())
        val all = Protocols(listOf(Protocol.All))


        fun isCLI(name:String): Boolean {
            return (name == Protocol.All.name || name == Protocol.CLI.name)
        }
    }
}