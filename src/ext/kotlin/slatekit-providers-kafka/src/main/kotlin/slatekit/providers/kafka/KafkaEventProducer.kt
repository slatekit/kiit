package slatekit.providers.kafka


import kotlinx.coroutines.CompletableDeferred
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.clients.producer.*
import slatekit.common.Identity
import slatekit.common.conf.Conf
import slatekit.common.log.Logger
import slatekit.core.eventing.EventConverter
import slatekit.core.eventing.EventProducer
import slatekit.core.eventing.EventTopic
import slatekit.core.eventing.EventUtils
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


/**
 * Reusable Kafka Event Publisher, with this there no need to create a custom EventX Producer.
 * You just have to supply a converter for Event ( payload ) -> Envelope ( headers + payload ) -> JSON.
 *
 * @param config    : application.conf for kafka settings
 * @param topicName : Standardized topic name with convention
 * @param converter : Converter payload -> envelope -> json
 * @param ec        : Needed
 * @tparam TPayload
 */
class KafkaEventProducer<TData, TEvent>(
        val config: Conf,
        topicName: String,
        val logger: Logger,
        override val converter: EventConverter<TData, TEvent>): EventProducer<TData, TEvent, RecordMetadata> {

    protected val kconfig  = KafkaConfig.load(config, true)
    protected val kafka = KafkaProducer<String, String>(kconfig.props())

    /**
     * Enforced naming convention for NEW topics ( {name} can not have "-", only "_" ).
     * This ensures the name can be easily split via "-" from the other parts.
     * {group}-{name}-{env}
     * E.g. "corenonprod-journal_events_all_v1-qat2"
     */
    override val topic: EventTopic = EventUtils.topic(topicName)

    /**
     * Simple identity of producer using topic name + UUID to distinguish this producer from other producer instances.
     * This allows for filtering the logs by producer if needed
     * {name}.{instance}
     * e.g. "journal_events_all_v1.123456
     */
    override val id: Identity = Identity.job( topic.name, "")

    /**
     * Publish typed event that will be converted to the full standardized envelope ( headers + payload )
     * @param data    : Event being sent, this will be added as the payload in the full envelope
     * @param key     : Optional key for partitioning ( e.g. user uuid )
     * @return
     */
    override suspend fun publish(data: TData, key:String?): Outcome<RecordMetadata> {
        return try {
            val envelope = converter.convert(data)
            val json = converter.encode(envelope)
            publish(envelope, key, json)
        } catch(ex:Exception) {
            Outcomes.unexpected<RecordMetadata>(ex)
        }
    }

    /**
     * Overloaded publish using the full EventEnvelope ( headers + payload - from common.kinesis ) directly.
     * This is just a convenience method as there are cases for explicitly providing the full envelope/json message.
     * E.g. We do dual publishing to kinesis + kafka now, so we can convert event -> envelope -> JSON in 1 go
     * and supply the envelope + json explicitly without 2 conversions
     * @param envelope : Common Kinesis envelope with headers + payload ( for compatibility ) right now
     * @param key      : Optional key for partitioning ( e.g. user uuid )
     * @param message  : Optional JSON representation of the envelope if available ( to avoid duplicate JSON serialization )
     * @return
     */
    override suspend fun publish(envelope: TEvent, key:String?, message: String?): Outcome<RecordMetadata> {
//        val promise = Job[Either[WWError, RecordMetadata]]()
//        publish(promise, envelope, key, message)
//        val result:Future[Either[WWError, RecordMetadata]] = promise.future
//        result
        return Outcomes.errored()
    }

    /**
     * Async based sending of events to the stream using Promises.
     * The promise is needed to make the call async as Kafka requires a callback for completion notification
     * @param event    : Common Kinesis envelope with headers + payload ( for compatibility ) right now
     * @param key      : Optional key for partitioning ( e.g. user uuid )
     * @param message  : Optional JSON representation of the envelope if available ( to avoid duplicate JSON serialization )
     * @return
     */
    override suspend fun publish(promise: CompletableDeferred<Outcome<RecordMetadata>>, event: TEvent, key:String?, message: String?):Unit {
        // NOTE: No need for object allocation via Try, plain "try" is good enough
        // at this level where we are publishing many events.
        try {
            // Handle error at serialization
            val finalMessage = message ?: converter.encode(event)

            // Ensure partitioning by key/uuid if supplied
            val record: ProducerRecord<String, String> = build(key, finalMessage)
            kafka.send(record) { metadata: RecordMetadata, ex: Exception? ->
                // Regardless of exception, we are modeling success/failure using Either.
                // Only unhandled exception ( see catch below ) is reason for promise.failure
                if (ex == null) {
                    //logger.info("Action='kafka-publish', success=true, status=succeeded, producer=${id.instance}, topic=$topic, id=${event.headers.id}, type=${event.headers.`type`}, action=${envelope.headers.action}, partition=${metadata.partition()}")
                    logger.info("Action='kafka-publish', success=true, status=succeeded, producer=${id.instance}, topic=$topic,  partition=${metadata.partition()}")
                    promise.complete(Outcomes.success(metadata))
                } else {
                    val msg = "Action='kafka-publish', success=false, status=unexpected, producer=${id.instance}, topic=$topic}"
                    logger.error(msg, ex)
                    promise.complete(Outcomes.unexpected(ex))
                }
            }
            // No need to flush, kakfa will handle that itself
            // kafka.flush()
        } catch(ex: Exception) {
            val msg = "Action='kafka-publish', success=false, status=unexpected, producer=${id.instance}, topic=$topic}"
            logger.error(msg, ex)
            promise.complete(Outcomes.unexpected(ex))
        }
    }

    /**
     * NON-PRODUCTION-USE: Synchronous based publishing.
     * NOTE: This was taken from DSAR, even though this is wrapped in a
     * future ( from publish method above ), this is technically still blocking via the "get" call.
     * It looks like this was done in order to retrieve the result of the send operation in kafka.
     * Have to check w/ the team on this!
     * @param event    : Common Kinesis envelope with headers + payload ( for compatibility ) right now
     * @param key      : Optional key for partitioning ( e.g. user uuid )
     * @param message  : Optional JSON representation of the envelope if available ( to avoid duplicate JSON serialization )
     * @return
     */
    override fun publishSync(event: TEvent, key:String?, message: String?): Outcome<RecordMetadata> {
        return try {
            // Ensure partitioning by key/uuid if supplied
            val finalMessage =  message ?: converter.encode(event)
            val record: ProducerRecord<String, String> = build(key, finalMessage)
            val future = kafka.send(record)
            val metadata = future.get()
            // No need to flush, kakfa will handle that itself
            // kafka.flush()
            //logger.info("Action='kafka-publish', success=true, status=succeeded, producer=${id.instance}, topic=$topic, id=${envelope.headers.id}, type=${envelope.headers.`type`}, action=${envelope.headers.action}, partition=${metadata.partition()}")
            logger.info("Action='kafka-publish', success=true, status=succeeded, producer=${id.instance}, topic=$topic, partition=${metadata.partition()}")
            Outcomes.success(metadata)
        } catch ( ex:Exception) {
            val msg = "Action='kafka-publish', success=false, status=unexpected, producer=${id.instance}, topic=$topic}"
            logger.error(msg, ex)
            Outcomes.unexpected(ex)
        }
    }

    override suspend fun flush() {

    }

    override suspend fun close() {

    }


    private fun build(key:String?, message:String): ProducerRecord<String, String> {
        val record: ProducerRecord<String, String> = when(key) {
            null -> ProducerRecord(topic.fullname, message)
            else -> ProducerRecord(topic.fullname, key, message)
        }
        return record
    }
}