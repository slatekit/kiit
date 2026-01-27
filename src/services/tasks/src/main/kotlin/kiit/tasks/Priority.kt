package kiit.tasks

import kiit.common.EnumLike
import kiit.common.EnumSupport

/**
 * General purpose priority Enum to be used for Queue to indicate
 * that 1 queue has a higher/lower priority that another queue and
 * therefore will be processed more often.
 */
enum class Priority(override val value: Int) : EnumLike {
    Low(1),
    Mid(2),
    High(3);

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(Low, Mid, High)
        }
    }
}