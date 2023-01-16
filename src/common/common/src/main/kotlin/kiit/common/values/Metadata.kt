package kiit.common.values

import kiit.common.values.Inputs

interface Metadata : Inputs {
    fun toMap(): Map<String, Any>
}
