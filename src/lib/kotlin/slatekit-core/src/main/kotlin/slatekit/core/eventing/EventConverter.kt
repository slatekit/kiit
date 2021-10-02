package slatekit.core.eventing

/**
 * Publishing an event to a stream in JSON contains a lot of boiler plate code.
 * ( This is much like writing a model to a database - also boiler plate )
 *
 * In both cases, essentially all we really need is a converter for
 * 1. stream  : convert from event ( body    ) -> envelope  -> JSON
 * 2. database: convert from model ( entity  ) -> jdbc stmt -> SQL
 *
 * This handles the core operations for an event to be put on a stream
 * 1. convert event   ( body ) -> envelope ( headers + payload )
 * 2. encode  envelope -> JSON
 *
 * Publishing any event to a stream then simply involves
 * 1. Building a converter, NOT a custom producer
 * 2. Reusing the @see:KafkaEventProducer
 *
 * @tparam TPayload
 * @tparam TEnvelope
 */
interface EventConverter<TPayload, TEvent> {

    /**
     * Converts the typed data to the standardized Event
     * @param data   : Just the data portion of an event ( meta + data )
     * @return
     */
    fun convert(data:TPayload):TEvent

    /**
     * Encodes the standardized Event to JSON
     * @param event : Full event ( meta + data )
     * @return
     */
    fun encode(event: TEvent):String
}