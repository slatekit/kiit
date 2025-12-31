package kiit.tasks

import kiit.common.EnumLike
import kiit.common.EnumSupport

enum class Sensitivity(override val value: Int) : EnumLike {
    Normal    (1),
    Protected (2),
    Sensitive (3);

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(Normal, Protected, Sensitive)
        }
    }
}