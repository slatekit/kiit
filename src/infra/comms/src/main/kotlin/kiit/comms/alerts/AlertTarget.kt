package kiit.comms.alerts

/**
 * Represents a target/destination for an alert.
 * E.g Could be a Slack Hook for a channel.
 *
 * @param target : Name of this target   e.g. "slack-signup-alerts"
 * @param channel: Id of the target      e.g. "ABC123"       : slack channel id
 * @param account: Account of the target e.g. "XYZ123"       : slack account id
 * @param name : Name of the target    e.g. "User Alerts"  : slack channel name
 * @param key : Api Key of the target e.g. "123456"       : slack api key
 * @param enabled: Whether or not this is enabled ( can be turned on/off at runtime )
 *
 */
open class AlertTarget(
    val target: String,
    val sender: String,
    val channel: String,
    val account: String,
    val name: String,
    val key: String,
    var enabled: Boolean
)
