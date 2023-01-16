package kiit.apis.core

import kiit.apis.ApiConstants

data class Roles(val all: List<String>) {

    val isEmpty = all.isEmpty() || all.size == 1 && all.first() == kiit.common.auth.Roles.NONE

    val allowGuest = all.any { it == kiit.common.auth.Roles.GUEST }

    val allowAll = all.any { it == kiit.common.auth.Roles.ALL }

    val isAuthed = !isEmpty && !allowGuest

    val isParentReference = !isEmpty && all.size == 1 && all.first() == ApiConstants.parent

    val delimited = all.joinToString()

    fun contains(name: String): Boolean = all.contains(name)

    fun orElse(other: Roles): Roles = if (this.isEmpty || this.isParentReference) other else this

    companion object {
        val empty = Roles(listOf())

        fun of(items: Array<String>): Roles {
            return if (items.isEmpty()) empty
            else Roles(items.toList())
        }
    }
}
