package slatekit.tracking

import org.threeten.bp.ZonedDateTime

/**
 * Simple gauge to check / refresh a value
 */
data class Gauge<T>(override val tags: List<Tag>,
                    val fetcher: () -> T,
                    val customTags:List<Tag>? = null,
                    val reloadSeconds: Long = 10) : Tagged {

    private var value: T? = null
    private var lastTimeStamp = ZonedDateTime.now()

    fun get(): T? {
        if(isOld()) {
            refresh()
        }
        return value
    }


    fun refresh(){
        set(fetcher())
    }


    fun set(newValue:T){
        value = newValue
        lastTimeStamp = ZonedDateTime.now()
    }


    fun isOld():Boolean {
        val now = ZonedDateTime.now()
        val expires = lastTimeStamp.plusSeconds(reloadSeconds)
        return now > expires
    }
}