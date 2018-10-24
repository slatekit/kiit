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

package slatekit.common.results

import slatekit.common.DateTime
import slatekit.common.ResultEx
import slatekit.common.info.Memory
import java.time.Duration

data class ResultTimed<T>(
        val desc: String,
        val start: DateTime,
        val end: DateTime,
        val duration: Duration,
        val result: ResultEx<T>,
        val memory: Memory?,
        val avg: Long?
) {
    val dur = start.durationFrom(end)

    /**
     *
     * @param start
     * @param end
     * @param duration
     * @param mem
     * @param data
     */
    constructor(start: DateTime, end: DateTime, duration: Duration, data: ResultEx<T>, mem: Memory?, avg: Long?)
            :
            this("", start, end, duration, data, mem, avg)


    fun print(): Unit {
        println("desc           : " + desc)
        println("start          : " + start)
        println("end            : " + end)
        println("duration.secs  : " + duration.seconds)
        println("duration.nanos : " + duration.nano)

        if (memory != null) {
            println("memory.used    : " + memory.used)
            println("memory.free    : " + memory.free)
            println("memory.total   : " + memory.total)
            println("memory.max     : " + memory.max)
            Unit
        }
        println()
    }


    companion object {

        @JvmStatic
        fun <T> build(started: DateTime, result: ResultEx<T>): ResultTimed<T> {
            val ended = DateTime.now()
            val duration = started.durationFrom(ended)
            return ResultTimed<T>("", started, ended, duration, result, null, null)
        }
    }

}