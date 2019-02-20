package slatekit.common

interface Metadata : Inputs {
    fun toMap(): Map<String, Any>
}
