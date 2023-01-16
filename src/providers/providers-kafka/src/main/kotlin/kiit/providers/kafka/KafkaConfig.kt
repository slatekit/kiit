package kiit.providers.kafka

import kiit.common.conf.Conf

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

data class KafkaConfig(val host:String, val user:String, val pswd:String, val saslEnabled:Boolean, val isProducer:Boolean) {

    private val jass:String = """org.apache.kafka.common.security.plain.PlainLoginModule required username="$user" password="$pswd";"""


    fun props(): Properties {
        val props = Properties()
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host)
        if (saslEnabled) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_SSL)
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN")
            props.put(SaslConfigs.SASL_JAAS_CONFIG, jass)
        }
        if(isProducer){
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.qualifiedName)
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.qualifiedName)
        }
        return props
    }


    companion object {
        fun load(conf: Conf, isProducer: Boolean): KafkaConfig {
            return KafkaConfig(
                    host = conf.getString(KafkaConstants.KAFKA_HOST),
                    user = conf.getString(KafkaConstants.KAFKA_USERNAME),
                    pswd = conf.getString(KafkaConstants.KAFKA_PASSWORD),
                    saslEnabled = conf.getBool(KafkaConstants.SASL_ENABLED),
                    isProducer = isProducer
            )
        }
    }
}