/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.common.io

import kiit.results.Outcome
import kiit.results.Try
import kiit.results.builders.Outcomes
import kiit.results.builders.Tries


interface IO<in I, out O> {

    fun perform(i: I): O
    fun attempt(i:I): Try<O> = Tries.of { perform(i) }
    fun outcome(i:I): Outcome<O> = Outcomes.of { perform(i) }
}


class Print(val io: ((Any?) -> Unit)? = null) : IO<Any?, Unit> {

    override fun perform(i: Any?) = when (io) {
        null -> print(i)
        else -> io.invoke(i)
    }
}


class Println(val io: ((Any?) -> Unit)? = null) : IO<Any?, Unit> {

    override fun perform(i: Any?) = when (io) {
        null -> println(i)
        else -> io.invoke(i)
    }
}


class Readln(val io: ((Unit) -> String?)? = null) : IO<Unit, String?> {

    override fun perform(i: Unit): String? = when (io) {
        null -> readLine()
        else -> io.invoke(Unit)
    }
}


class StringWriter(private val buffer: StringBuilder) : IO<Any?, Unit> {

    override fun perform(i: Any?) {
        buffer.append(i)
    }
}