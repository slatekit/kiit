package slatekit.core.eventing

import kotlinx.coroutines.CompletableDeferred
import slatekit.common.Identity
import slatekit.results.Outcome

/**
 * Interface for any event producer
 * Note: An event is structured as
 *
 * event: {
 *      meta: {
 *      }
 *      data: {
 *      }
 * }
 * @tparam TData     : Payload type    ( e.g. SignupUser )
 * @tparam TEvent    : Full event type ( e.g. SignupEvent  )
 * @tparam TResult   : Result type     ( e.g. RecordMetadata - for kafka )
 */
interface EventProducer<TData, TEvent, TResult> {

    /**
     * Identifies producer with name, uuid ( used in logging )
     * This allows to more easily see / filter logs tied to a single producer
     * e.g.
     * {area}-{service}-{name}-{version}-{env}
     * "signup-registration-all-v1-qat
     */
    val id: Identity

    /**
     * E.g. {area}-{name}-{env}
     * E.g. "signup-registration-qat"
     */
    val topic: EventTopic

    /**
     * Convert from payload -> envelope -> json
     */
    val converter: EventConverter<TData, TEvent>

    /**
     * Publishing to stream using the typed data converted to the standardized Event
     * @param data   : Just the data portion of an event ( meta + data )
     * @param key    : Optional key for partitioning ( e.g. user uuid )
     * @return
     */
    suspend fun publish(data: TData, key:String?): Outcome<TResult>

    /**
     * Publish using the raw EventEnvelope ( useful for testing )
     * @param event    : Full event ( meta + data )
     * @param key      : Optional key for partitioning ( e.g. user uuid )
     * @param message  : Optional JSON representation of the event if available
     * @return
     */
    suspend fun publish(event:TEvent, key:String?, message: String?): Outcome<TResult>

    /**
     * Publish using promise to get back the success/failure
     * @param job     : For notification when suspended job is complete
     * @param event   : Full event ( meta + data )
     * @param key     : Optional key for partitioning ( e.g. user uuid )
     * @param message : Optional JSON representation of the event if available
     */
    suspend fun publish(promise: CompletableDeferred<Outcome<TResult>>, event: TEvent, key:String?, message: String?)

    /**
     * Publish using a synchronous approach to get back the success/failure
     * @param event   : Full event ( meta + data )
     * @param key     : Optional key for partitioning ( e.g. user uuid )
     * @param message : Optional JSON representation of the event if available
     */
    fun publishSync(event: TEvent, key:String?, message: String?): Outcome<TResult>

    /**
     * Flushes all buffered events
     */
    suspend fun flush(): Unit

    /**
     * Close the underlying producer
     */
    suspend fun close(): Unit
}