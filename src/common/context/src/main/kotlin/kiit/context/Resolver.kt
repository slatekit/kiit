package kiit.context

import kotlin.reflect.KClass

interface Resolver {
    fun <T> contains(cls:String):Boolean
    fun <T> resolve(cls:KClass<*>): T
    fun <T> resolve(cls:KClass<*>, name: String): T
}


inline fun <reified T> Resolver.resolve():T = this.resolve(T::class)
inline fun <reified T> Resolver.resolve(name:String):T = this.resolve(T::class, name)