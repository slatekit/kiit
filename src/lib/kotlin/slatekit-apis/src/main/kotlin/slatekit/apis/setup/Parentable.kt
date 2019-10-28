package slatekit.apis.setup

import slatekit.apis.ApiConstants

interface Parentable<T> {
    val name: String

    fun orElse(other: T): T {
        return if (this.name == ApiConstants.parent) other else this as T
    }
}
