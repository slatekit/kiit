package slatekit.common

/**
 * Represents a group of items with contextual info
 * @param name   : Name for the group         e.g. "Favorites"
 * @param source : Identifies source of data  e.g. userid | group | recent
 * @param items  : Data items in this group   e.g. List<TodoItem>
 */
data class Group<out T>(
    val name: String,
    val source: String,
    val items: List<T>
) {

    fun isEmpty(): Boolean = items.isEmpty()
}
