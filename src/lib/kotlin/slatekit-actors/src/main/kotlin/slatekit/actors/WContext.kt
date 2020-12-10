package slatekit.actors

import java.util.concurrent.atomic.AtomicReference

data class WContext<T>(val worker: Worker<T>, val status: AtomicReference<Status> = AtomicReference(Status.InActive))
