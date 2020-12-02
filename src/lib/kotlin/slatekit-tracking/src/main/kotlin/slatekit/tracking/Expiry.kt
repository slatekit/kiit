package slatekit.tracking

import slatekit.common.DateTime

data class Expiry(val seconds:Long,
                  val started:DateTime = DateTime.now(),
                  val expires:DateTime = started.plusSeconds(seconds)) {

    fun isExpired(): Boolean {
        return if (seconds <= 0) false else expires <= DateTime.now()
    }


    fun isAlive():Boolean = !isExpired()


    fun expire(): Expiry = copy(expires = DateTime.now())


    fun extend(): Expiry = copy(expires = DateTime.now().plusSeconds(seconds.toLong()))

}
