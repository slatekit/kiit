package slatekit.cache


/**
 * The LinkedHashMap already has LRU(Least Recently Used) behaviour out of the box.
 * The LRUMap is simply an derived implementation with removeEldestEntry implemented!
 * @see
 * 1. https://www.geeksforgeeks.org/design-a-data-structure-for-lru-cache/
 * 2. https://dzone.com/articles/java-based-simple-cache-lru-eviction
 * 3. https://leetcode.com/problems/lru-cache/discuss/45939/Laziest-implementation:-Java%27s-LinkedHashMap-takes-care-of-everything
 */
class LRUMap<K, V>(private val capacity: Int)
    : LinkedHashMap<K, V>(capacity + 1, 1.0f, true) {

    override fun removeEldestEntry(entry:Map.Entry<K, V>?): Boolean {
        return size > this.capacity
    }
}
