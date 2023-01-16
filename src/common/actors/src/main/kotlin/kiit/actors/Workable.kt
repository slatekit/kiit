package kiit.actors

import kotlinx.coroutines.Job

interface Workable {
    /**
     * Launches work ( e.g. by processing all items in a channel )
     */
    suspend fun work(): Job
}


