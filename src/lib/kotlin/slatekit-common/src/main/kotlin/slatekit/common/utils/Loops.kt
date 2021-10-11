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


/**
 * alternatives to using do while loops and iterations where you want to stop
 */
object Loops {

    /**
     * "do while" loop alternative using tailrec to avoid a "variable" for checking conditions
     *
     * @param condition
     */
    tailrec fun doUntil(condition: () -> Boolean) {
        // simulate do while with call by name ( evaluate first )
        if (condition())
            doUntil(condition)
    }

    /**
     * "do while" loop alternative with index/count using tail recursion
     *
     * @param max
     * @param condition
     */
    fun doUntilIndex(max: Int, condition: (Int) -> Boolean) {

        tailrec fun rep(ndx: Int, max: Int, condition: (Int) -> Boolean) {
            if (ndx < max && condition(ndx)) {
                rep(ndx + 1, max, condition)
            }
        }

        // do : run first
        if (max > 0) {
            val first = condition(0)
            if (first) {
                rep(1, max, condition)
            }
        }
    }

    /**
     * "takeWhile" iteration alternative.
     * this provides a way for the caller to dictate the next index.
     *
     * NOTE: this is ideal for low-level character / string / lexical parsing
     * @param condition
     */

    tailrec fun repeatWithIndex(ndx: Int, end: Int, condition: (Int) -> Int): Int {
        val nextIndex = condition(ndx)
        return if (nextIndex >= end)
            nextIndex
        else
            repeatWithIndex(nextIndex, end, condition)
    }


    /**
     * "takeWhile" iteration alternative.
     * this provides a way for the caller to dictate the next index along with a condition
     * to indicate continued traversal
     *
     * @param condition
     */

    tailrec fun repeatWithIndexAndBool(ndx: Int, end: Int, condition: (Int) -> Pair<Boolean, Int>): Int {
        val result = condition(ndx)
        val success = result.first
        val nextIndex = result.second
        return if (!success)
            ndx
        else
            repeatWithIndexAndBool(nextIndex, end, condition)
    }
}
