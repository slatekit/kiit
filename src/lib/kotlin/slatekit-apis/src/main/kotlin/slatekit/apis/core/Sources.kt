package slatekit.apis.core

import slatekit.common.Source

data class Sources(val all: List<Source>) {

    val isEmpty:Boolean = all.isEmpty()

    val isParentReference = all.size == 1 && all.first().isParentReference()

    fun hasCLI(): Boolean {
        return all.any { it == Source.CLI || it == Source.All }
    }

    fun hasAPI(): Boolean {
        return all.any { it == Source.API || it == Source.All }
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

    fun isMatchOrAll(expected:List<Source>):Boolean {
        return expected.any { isMatchOrElse(it, Source.All) }
    }

    fun isMatchOrElse(expected:Source, other:Source):Boolean {
        return when {
            all.contains(expected) -> true
            all.contains(other) -> true
            else -> false
        }
    }

    fun orElse(other: Sources): Sources = if (this.all.isEmpty()) other else this

    companion object {
        val empty = Sources(listOf())
        val all = Sources(listOf(Source.All))

        fun of(items: Array<String>): Sources {
            return if (items.isEmpty()) all
            else Sources(items.toList().map { Source.parse(it) })
        }
    }
}
