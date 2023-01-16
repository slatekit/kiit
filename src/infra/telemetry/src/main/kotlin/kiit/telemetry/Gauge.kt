package kiit.telemetry

import java.util.concurrent.atomic.AtomicReference

/**
 * Simple gauge to check / refresh a value
 */
data class Gauge<T>(override val tags: List<Tag>,
                    val fetcher: () -> T,
                    val customTags:List<Tag>? = null,
                    val reloadSeconds: Long = 10) : Tagged {

    private val value = AtomicReference<T?>(null)
    private val expiry = Expiry(reloadSeconds)

    fun get(): T? {
        if(isExpired()) {
            refresh()
        }
        return value.get()
    }


    fun refresh(){
        set(fetcher())
    }


    fun set(newValue:T){
        value.set(newValue)
        expiry.extend()
    }


    fun isExpired():Boolean = expiry.isExpired()
}
