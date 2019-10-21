package slatekit.jobs.events

import slatekit.common.Status


interface Events<T> {

    /**
     * Subscribes clients to a change of @see[slatekit.common.Status] on any item in this component
     * @param op:Operation to call when status is changed
     */
    suspend fun subscribe(op:suspend (T) -> Unit )


    /**
     * Subscribes clients to a change to the specific status @see[slatekit.common.Status]
     * @param op:Operation to call when status is changed
     */
    suspend fun subscribe(status: Status, op:suspend (T) -> Unit )
}



