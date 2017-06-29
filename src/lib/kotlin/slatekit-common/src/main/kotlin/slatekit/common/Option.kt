/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common

/*
sealed class Option<out T> {

    abstract fun empty(): Boolean


    abstract fun get(): T


    fun exists(): Boolean = !empty()
    fun isDefined(): Boolean = !empty()


    inline fun <U> map(f: (T) -> U): Option<U> = if (empty()) {
        None
    } else {
        Some(f(get()))
    }


    inline fun <U> flatMap(f: (T) -> Option<U>): Option<U> = if (empty()) {
        None
    } else {
        f(get())
    }


    inline fun <U> fold(emptyVal:() -> U, f: (T) -> U): U = if (empty()) {
        emptyVal()
    } else {
        f(get())
    }



    class Some<out T>( val v:T? ) : Option<T>() {

        override fun get():T = v!!

        override fun empty():Boolean = v == null
    }


    object None : Option<Nothing>() {
        override fun empty():Boolean = true
        override fun get() = throw NoSuchElementException("None.get")
    }
}


fun <T> Option<T>.getOrElse(default:  T): T = if (empty()) {
    default
} else {
    get()
}
*/
