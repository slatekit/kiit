package kiit.tasks

import kiit.common.EnumLike
import kiit.common.EnumSupport

/**
 * Represents how an @see[Action] runs.
 */
enum class Mode(override val value: Int) : EnumLike {
    Adhoc  (1),
    Repeat (2),
    Queued (3);

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(Adhoc, Repeat, Queued)
        }
    }
}


