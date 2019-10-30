package slatekit.apis.setup

import slatekit.apis.ApiConstants

interface Parentable<T> {
    val name: String

    fun isParentReference():Boolean {
        return this.name == ApiConstants.parent
    }

    fun orElse(other: T): T {
        return if (isParentReference()) other else this as T
    }
}
