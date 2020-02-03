package slatekit.policy.throttle


sealed class Rate {
    object Zero : Rate()
    object Low  : Rate()
    object Mid  : Rate()
    object High : Rate()
}
