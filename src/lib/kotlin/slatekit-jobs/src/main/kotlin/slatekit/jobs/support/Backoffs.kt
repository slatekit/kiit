package slatekit.jobs.slatekit.jobs.support

import slatekit.common.paged.Pager

class Backoffs(val times: Pager<Long> = default){
    private var isOn = false

    fun next():Long {
        return when(isOn){
            true  -> times.next()
            false -> times.current(moveNext = true)
        }
    }

    fun reset() {
        isOn = false
        times.reset()
    }


    companion object {
        val default = Pager<Long>(listOf(2, 4, 8, 16, 32, 64, 128, 256), true, 0)
    }
}
