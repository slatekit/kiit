package slatekit.common

/**
 * Allows safe paging and navigation in various ways including:
 *
 * 1. forward  via next()
 * 2. backward via back()
 * 3. circular via feature flag passed in
 *    moving next at end position will move it back to the start ( 0 index  )
 *    moving back at start position will move it back to the end ( size - 1 )
 * 4. move to a specific position via move
 */
class Pager<T>(
        val list: List<T>,
        val circular: Boolean
) {
    private var index = 0

    val size: Int = list.size

    val start: Int = 0

    val end: Int = list.size - 1

    fun pos(): Int = index

    fun first(): T = list[start]

    fun last(): T = list[end]

    fun current(): T = list[index]

    fun isAtStart(): Boolean = pos() == start

    fun isAtEnd(): Boolean = pos() == end

    fun canMoveNext(): Boolean = !isAtEnd() || circular

    fun canMoveBack(): Boolean = !isAtStart() || circular

    fun reset(): Int {
        index = 0
        return index
    }

    fun moveFirst():Int = move(start)

    fun moveLast():Int = move(end)

    fun move(desired: Int): Int {
        val next = when {
            desired < 0 -> index
            desired >= size -> index
            else -> desired
        }
        index = next
        return index
    }

    fun next(): T {
        index = when {
            isAtEnd() && circular -> start
            isAtEnd() && !circular -> index
            else -> index + 1
        }
        return list[index]
    }

    fun back(): T {
        index = when {
            isAtStart() && circular -> end
            isAtStart() && !circular -> index
            else -> index - 1
        }
        return list[index]
    }
}