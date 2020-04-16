package slatekit.common.utils

/**
 * Represents a group of items with contextual info
 * @param name   : Name for the group           e.g. "Favorites"
 * @param source : Identifies source of data    e.g. userid | group | recent
 * @param items  : Data items in this group     e.g. List<TodoItem>
 * @param total  : Total of some group value    e.g. Can be used to aggregate a group property
 * @param tag    : Reference / Correlation id   e.g. Group / Batch id
 */
data class Group<out T>(
    @JvmField val name: String,
    @JvmField val source: String,
    @JvmField val items: List<T>,
    @JvmField val total:Int = items.size,
    @JvmField val tag: String = ""
) {

    fun isEmpty(): Boolean = items.isEmpty()
}
