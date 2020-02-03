package slatekit.jobs.slatekit.jobs.support

import slatekit.common.paged.Pager

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
