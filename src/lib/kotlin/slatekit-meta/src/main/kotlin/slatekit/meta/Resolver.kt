package slatekit.meta

import kotlin.reflect.KClass

interface Resolver {
    //fun <T> contains(cls:KClass<*>):Boolean
    fun <T> obtain(cls:KClass<*>): T
    fun <T> obtain(cls: KClass<*>, name: String): T
}


inline fun <reified T> Resolver.resolve():T {
    return this.obtain(T::class)
}