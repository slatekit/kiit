package slatekit.apis.core

import slatekit.apis.Protocol

data class Protocols(val all:List<Protocol>) {

    fun hasCLI(): Boolean {
        return all.any {  isCLI(it.name) }
    }


    fun hasWeb(): Boolean {
        return all.any {  isWeb(it.name) }
    }


    fun orElse(other:Protocols):Protocols = if(this.all.isEmpty()) other else this


    companion object {
        val empty = Protocols(listOf())
        val all = Protocols(listOf(Protocol.All))


        fun of(items:Array<String>):Protocols {
            return if(items.isEmpty()) all
            else Protocols(items.toList().map { Protocol.parse(it) })
        }


        fun isCLI(name:String): Boolean {
            return (name == Protocol.All.name || name == Protocol.CLI.name)
        }


        fun isWeb(name:String): Boolean {
            return (name == Protocol.All.name || name == Protocol.Web.name)
        }
    }
}