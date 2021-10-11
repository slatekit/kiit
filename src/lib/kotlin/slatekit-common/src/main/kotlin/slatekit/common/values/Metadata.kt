package slatekit.common.values

import slatekit.common.values.Inputs

interface Metadata : Inputs {
    fun toMap(): Map<String, Any>
}
