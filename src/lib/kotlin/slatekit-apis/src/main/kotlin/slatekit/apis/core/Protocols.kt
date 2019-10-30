package slatekit.apis.core

import slatekit.apis.Protocol

data class Protocols(val all: List<Protocol>) {

    val isEmpty:Boolean = all.isEmpty()

    val isParentReference = all.size == 1 && all.first().isParentReference()

    fun hasCLI(): Boolean {
        return all.any { it == Protocol.CLI || it == Protocol.All }
    }

    fun hasWeb(): Boolean {
        return all.any { it == Protocol.Web || it == Protocol.All }
    }

    fun isMatchExact(expected:Protocol):Boolean {
        return when {
            all.contains(expected)     -> true
            else -> false
        }
    }

    fun isMatchOrAll(expected:Protocol):Boolean {
        return isMatchOrElse(expected, Protocol.All)
    }

    fun isMatchOrElse(expected:Protocol, other:Protocol):Boolean {
        return when {
            all.contains(expected)     -> true
            all.contains(other) -> true
            else -> false
        }
    }

    fun orElse(other: Protocols): Protocols = if (this.all.isEmpty()) other else this

    companion object {
        val empty = Protocols(listOf())
        val all = Protocols(listOf(Protocol.All))

        fun of(items: Array<String>): Protocols {
            return if (items.isEmpty()) all
            else Protocols(items.toList().map { Protocol.parse(it) })
        }
    }
}
