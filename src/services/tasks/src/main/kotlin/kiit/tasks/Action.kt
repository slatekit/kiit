package kiit.tasks

import kiit.common.Identity
import kiit.results.Outcome
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

/**
 * This is the main component of this module.
 * This represents an operation that can perform some work.
 * 1. Function: This is typically just a function / method
 * 2. Mode    : This can be called adhoc, scheduled, or invoked from a queue
 * 3. Control : Can be gracefully controlled ( started, stopped, paused, resumed ).
 */
data class Action(
    val id: Identity,
    val name: String,
    val mode:Mode,
    val op: (suspend (Task) -> Outcome<*>)? = null,
    val call: Call? = null,
    val status: AtomicReference<State> = AtomicReference<State>(State(Status.InActive, Status.InActive.name)),
    val priority: Priority = Priority.Mid,
    val options: Options = Options()
) {
    val fullName = "${id.area}.${id.service}.${name}"
}

/**
 * Represents the actual function / method that handles the work.
 */
data class Call(
    val klass: KClass<*>,
    val member: KCallable<*>,
    val instance: Any
)
