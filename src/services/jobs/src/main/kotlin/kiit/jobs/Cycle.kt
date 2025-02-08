package kiit.jobs

/**
 * Life-cycle methods ( future state )
 */
interface Cycle {

    suspend fun onStarted() {
    }


    suspend fun onPaused(note:String?) {
    }


    suspend fun onResumed(note:String?) {
    }


    suspend fun onStopped(note:String?) {
    }


    suspend fun onFailed(note:String?) {
    }


    suspend fun onCompleted(note:String?) {
    }


    suspend fun onKilled(note:String?) {
    }
}
