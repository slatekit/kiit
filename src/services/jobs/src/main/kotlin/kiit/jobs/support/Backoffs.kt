package kiit.jobs.support

import slatekit.utils.paged.Pager

/**
 * Used to cycle through exponential "backoff" time in seconds
 * This can be used to back-off from processing a job/worker.
 * e.g.
 * 1, 2, 4, 8, 16, 32, 64
 */
class Backoffs(val times: Pager<Long> = times()){
    private var isOn = false

    fun next():Long {
        return when(isOn){
            true  -> {
                times.next()
            }
            false -> {
                isOn = true
                val currSec = times.current(moveNext = true)
                currSec
            }
        }
    }

    fun reset() {
        isOn = false
        times.reset()
    }


    fun curr():Long {
        return times.current()
    }


    companion object {

        fun default() = Backoffs(times())

        fun times() = Pager<Long>(listOf(2, 4, 8, 16, 32, 64, 128, 256), true, 0)
    }
}
