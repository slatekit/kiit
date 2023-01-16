package slatekit.core.eventing

/**
 * Naming convention enforced on the event topic
 * @param area    : Logical area of topic   e.g. "signup"
 * @param name    : Short name of topic     e.g. "subscribers"
 * @param env     : Environment             e.g. "qat" | "pro"
 */
data class EventTopic(val area:String, val name:String, val env:String) {

    /**
     * {area}-{name}-{env}
     * signup-subscribers-qat
     */
    val fullname = "${EventUtils.clean(area)}-${EventUtils.clean(name, false)}-${EventUtils.clean(env)}"
}