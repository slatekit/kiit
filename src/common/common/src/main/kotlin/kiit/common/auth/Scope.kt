package kiit.common.auth

/**
 * @param resource A named resource such as a service/api e.g. "account"
 * @param access A permission to the resource             e.g. "read | write"
 */
data class Scope(val resource:String, val access:String) {
    companion object {
        fun of(scope:String) : Scope {
            val parts = scope.trim().split(':')
            return Scope(parts[0], parts[1])
        }
    }
}