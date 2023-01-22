package kiit.common.values

interface Metadata : Inputs {
    fun toMap(): Map<String, Any>
}
