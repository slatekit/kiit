package slatekit.meta

import kotlin.reflect.KClass

interface Resolver {
    //fun <T> contains(cls:KClass<*>):Boolean
    fun <T> resolve(cls:KClass<*>): T
    fun <T> resolve(cls: KClass<*>, name: String): T
}


inline fun <reified T> Resolver.resolve():T {
    return this.resolve(T::class)
}