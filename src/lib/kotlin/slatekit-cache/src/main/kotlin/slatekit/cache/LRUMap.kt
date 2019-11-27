package slatekit.cache


/**
 * The LinkedHashMap already LRU(Least Recently Used) behaviour out of the box.
 * The LRUMap is simply an derived implementation with removeEldestEntry implemented!
 */
class LRUMap<K, V>(private val capacity: Int)
    : LinkedHashMap<K, V>(capacity + 1, 1.0f, true) {

    override fun removeEldestEntry(entry:Map.Entry<K, V>?): Boolean {
        return size > this.capacity
    }
}
