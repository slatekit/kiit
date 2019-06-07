package slatekit.common.ext

/**
 * Returns all items after the first
 */
fun <T> List<T>.tail(): List<T> = this.drop(1)

/**
 * Returns all items after the first
 */
fun <T> List<T>.update(index: Int, newValue: T): List<T> {
    require(index >= 0) { "Requested element index $index is less than zero." }
    require(index < this.size) { "Requested element index $index is more than the size." }
    return this.mapIndexed { ndx, value -> if (ndx == index) newValue else value }
}

/**
 * Inserts an item at the supplied index
 */
fun <T> List<T>.insertAt(index: Int, newValue: T): List<T> {
    require(index >= 0) { "Requested element index $index is less than zero." }
    require(index < this.size) { "Requested element index $index is more than the size." }
    return this.subList(0, index) + listOf(newValue) + this.subList(index, this.size)
}

/**
 * Removes the item at the supplied index
 */
fun <T> List<T>.removeAt(index: Int): List<T> {
    require(index >= 0) { "Requested element index $index is less than zero." }
    require(index < this.size) { "Requested element index $index is more than the size." }
    return this.filterIndexed { ndx, _ -> ndx != index }
}

/**
 * Converts the list to an immutable map
 */
fun <T> List<T>.convertToMap(): Map<T, T> = this.map { value -> Pair(value, value) }.toMap()

