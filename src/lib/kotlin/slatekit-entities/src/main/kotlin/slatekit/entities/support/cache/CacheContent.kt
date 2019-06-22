package slatekit.entities.support.cache

/**
 * Contains the cached items
 */
data class CacheContents<T>(
        val itemListCopy:List<T> = listOf(),
        val itemList:List<T> = listOf(),
        val itemIdMap:Map<String,T> = mapOf(),
        val itemKeyMap:Map<String, T> = mapOf()
)