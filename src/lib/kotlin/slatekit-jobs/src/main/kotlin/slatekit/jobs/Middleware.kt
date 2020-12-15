package slatekit.jobs

import slatekit.actors.Message

interface Middleware {
    /**
     * @param mgr   : Manager instance running the job
     * @param source: "WORK"
     * @param message: Message to intercept
     * @param next   : Next operation to call to proceed
     */
    suspend fun handle(mgr: Manager, source:String, message: Message<*>, next:suspend(Message<*>) -> Unit)
}
