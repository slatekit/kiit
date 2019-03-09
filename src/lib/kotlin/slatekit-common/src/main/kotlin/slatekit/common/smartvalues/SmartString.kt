package slatekit.common.smartvalues


open class SmartString<T>(
        override val required:Boolean,
        override val min: Int,
        override val max: Int,
        override val metadata: SmartMetadata) : SmartStr