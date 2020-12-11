package slatekit.actors

/**
 * Life-cycle methods used for a Worker
 */
interface Cycle {

    suspend fun started() {
    }


    suspend fun paused(note:String?) {
    }


    suspend fun resumed(note:String?) {
    }


    suspend fun stopped(note:String?) {
    }


    suspend fun failed(note:String?) {
    }


    suspend fun completed(note:String?) {
    }


    suspend fun killed(note:String?) {
    }
}
