/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.values

/**
 * Sorted, readonly map
 */
open class ListMap<A, B>(protected val list: List<Pair<A, B>> = listOf()) {

    protected val map = convert(list)

    /**
     * size of the list
     * @return
     */
    val size: Int = list.size

    /**
     * whether there is a key with the supplied name
     * @param key
     * @return
     */
    fun contains(key: A): Boolean = map.contains(key)

    /** Checks if this map maps `key` to a value and return the
     *  value if it exists.
     *
     *  @param key the key of the mapping of interest
     *  @return the value of the mapping, if it exists
     */
    operator fun get(key: A): B? {
        if (!map.containsKey(key)) return null
        val ndx = map[key]
        return if (ndx == null) null else list[ndx].second
    }

    /**
     * gets the value at the supplied index position.
     * @param pos
     * @return
     */
    fun getAt(pos: Int): B? = list[pos].second

    /**
     * Adds a new entry
     * @param kv
     * @return
     */
    operator fun plus(item: Pair<A, B>): ListMap<A, B> = add(item)

    /**
     * removes the key/value pair associated with the key
     * @param key
     */
    operator fun minus(key: A): ListMap<A, B> = remove(key)

    /**
     * adds a key/value to this collection
     * @param key
     * @param value
     */
    fun add(key: A, value: B): ListMap<A, B> {
        return add(Pair(key, value))
    }

    open fun add(item: Pair<A, B>): ListMap<A, B> {
        val newList = list.toMutableList()
        newList.add(item)
        return ListMap(newList)
    }

    /**
     * removes the key/value pair associated with the key
     * @param key
     */
    open fun remove(key: A): ListMap<A, B> {
        val allowed = list.filter { it.first != key }
        return ListMap(allowed)
    }

    open fun clone(): ListMap<A, B> {
        val copies = list.map { it -> Pair(it.first, it.second) }
        return ListMap(copies)
    }

    fun keys(): List<A> = list.map { it.first }

    fun values(): List<B> = list.map { it.second }

    fun entries(): List<Pair<A, B>> = list

    fun all(): List<B> = values()

    /**
     * iterates over each key/value pair and supplies it to the callback
     * @param callback
     */
    fun each(callback: (Int, A, B) -> Unit) {
        list.mapIndexed { index, pair -> callback(index, pair.first, pair.second) }
    }

    fun toMap(): Map<String, Any> = map.map { entry -> entry.key.toString() to list[entry.value].second as Any }.toMap()

    companion object {

        @JvmStatic
        fun <A, B> convert(items: List<Pair<A, B>>): Map<A, Int> {
            val map = mutableMapOf<A, Int>()
            items.forEachIndexed { index, pair -> map[pair.first] = index }
            return map.toMap()
        }
    }
}
