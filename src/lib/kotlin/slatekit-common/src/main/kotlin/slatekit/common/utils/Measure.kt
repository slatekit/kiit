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

package slatekit.common.utils

import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.ResultEx
import slatekit.common.info.Memory
import slatekit.common.results.ResultTimed

object Measure {

    /**
     * benchmarks an operation once
     *
     * @param desc : description of the operation
     * @param callback : the callback to execute
     * @return
     */
    fun <T> once(desc: String, callback: () -> T): ResultTimed<T> {

        val res =
            wrap<T>(desc, 1, { Result.attempt({ callback() }) })
        return res
    }

    /**
     * benchmarks an operation several times and returns a list of benchmarked results
     *
     * @param desc : description of the operation
     * @param callback : the callback to execute
     * @param count : the number of times to run
     * @return
     */
    fun <T> many(desc: String, callback: (Int) -> T, count: Int): List<ResultTimed<T>> {

        val results = (0..count).map {
            wrap(
                desc,
                count,
                { Result.attempt { callback(it) } })
        }
        return results.toList()
    }

    /**
     * benchmarks an operation several times and returns an average duration for each run
     *
     * @param desc : description of the operation
     * @param callback : the callback to execute
     * @param count : the number of times to run
     * @return
     */
    fun <T> avg(desc: String, count: Int, callback: (Int) -> T): ResultTimed<T> {

        // Repeat X times
        val results = 1.until(count).map { ndx ->

            // Wrap the whole call with the timer
            wrap<T>(desc, ndx, {

                // Attempt ( try/catch ) the callback which returns Result[T]
                Result.attempt({ callback(ndx) })
            })
        }

        // First / last
        val start = results.first()
        val end = results.last()

        // Duration of whole thing
        val duration = start.start.durationFrom(end.end)

        // Average them out
        val totalMs = results.fold(0L, { total, res -> total + res.duration.toMillis() })
        val avgMs = totalMs / count

        // Finally the result
        return ResultTimed(desc, start.start, end.end, duration, end.result, end.memory, avgMs)
    }

    fun <T> wrap(desc: String, count: Int, callback: () -> ResultEx<T>): ResultTimed<T> {

        // Start time & memory info
        val started = DateTime.now()
        val runtime = Runtime.getRuntime()
        val freeBefore = runtime.freeMemory()

        val result = callback()

        // End time and memory info
        val ended = DateTime.now()
        val freeAfter = runtime.freeMemory()

        // Diff: Time
        val duration = started.durationFrom(ended)

        // Avg : Time
        val ms = duration.toMillis()

        // Diff: memory
        val used = freeBefore - freeAfter
        val memory = Memory(used, runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory())

        return ResultTimed(desc, started, ended, duration, result, memory, ms)
    }
}
