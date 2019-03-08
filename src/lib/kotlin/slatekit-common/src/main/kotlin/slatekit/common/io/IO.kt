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


object Print : IO<Any?, Unit> {

    override fun run(i: Any?) {
        print(i)
    }
}

object Println : IO<Any?, Unit> {

    override fun run(i: Any?) {
        println(i)
    }
}

object Readln : IO<Any?, String?> {

    override fun run(i: Any?):String? = readLine()
}

class StringWriter(private val buffer: StringBuilder) : IO<Any?, Unit> {

    override fun run(i: Any?) {
        buffer.append(i)
    }
}