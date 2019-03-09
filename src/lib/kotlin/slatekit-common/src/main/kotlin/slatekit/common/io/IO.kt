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

package slatekit.common.io


interface IO<in I, out O> {

    fun run(i: I): O
}


class Print(val io: ((Any?) -> Unit)? = null) : IO<Any?, Unit> {

    override fun run(i: Any?) = when (io) {
        null -> print(i)
        else -> io.invoke(i)
    }
}


class Println(val io: ((Any?) -> Unit)? = null) : IO<Any?, Unit> {

    override fun run(i: Any?) = when (io) {
        null -> println(i)
        else -> io.invoke(i)
    }
}


class Readln(val io: ((Unit) -> String?)? = null) : IO<Unit, String?> {

    override fun run(i: Unit): String? = when (io) {
        null -> readLine()
        else -> io.invoke(Unit)
    }
}


class StringWriter(private val buffer: StringBuilder) : IO<Any?, Unit> {

    override fun run(i: Any?) {
        buffer.append(i)
    }
}