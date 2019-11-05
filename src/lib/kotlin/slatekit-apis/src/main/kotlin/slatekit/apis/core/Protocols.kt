package slatekit.apis.core

import slatekit.common.Source

data class Protocols(val all: List<Source>) {

    val isEmpty:Boolean = all.isEmpty()

    val isParentReference = all.size == 1 && all.first().isParentReference()

    fun hasCLI(): Boolean {
        return all.any { it == Source.CLI || it == Source.All }
    }

    fun hasWeb(): Boolean {
        return all.any { it == Source.Web || it == Source.All }
    }

    fun isMatchExact(expected:Source):Boolean {
        return when {
            all.contains(expected)     -> true
            else -> false
        }
    }

    fun isMatchOrAll(expected:Source):Boolean {
        return isMatchOrElse(expected, Source.All)
    }

    fun isMatchOrElse(expected:Source, other:Source):Boolean {
        return when {
            all.contains(expected)     -> true
            all.contains(other) -> true
            else -> false
        }
    }

    fun orElse(other: Protocols): Protocols = if (this.all.isEmpty()) other else this

    companion object {
        val empty = Protocols(listOf())
        val all = Protocols(listOf(Source.All))

        fun of(items: Array<String>): Protocols {
            return if (items.isEmpty()) all
            else Protocols(items.toList().map { Source.parse(it) })
        }
    }
}
