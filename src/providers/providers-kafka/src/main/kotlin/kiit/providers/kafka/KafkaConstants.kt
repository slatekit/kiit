package kiit.providers.kafka

object KafkaConstants {
    val LOCALHOST                  = "localhost"
    val KAFKA_ROOT                 = "kafka"
    val KAFKA_HOST                 = "$KAFKA_ROOT.hosts"
    val KAFKA_USERNAME             = "$KAFKA_ROOT.username"
    val KAFKA_PASSWORD             = "$KAFKA_ROOT.password"
    val SASL_ENABLED               = "$KAFKA_ROOT.sasl.enabled"
    val KAFKA_FEATURE_ENABLED      = "$KAFKA_ROOT.feature.enabled"
}