package slatekit.utils.paged

import slatekit.results.Outcome


data class PagedState<TOffset, TValue>(val batch: Long,
                                       val offset: TOffset,
                                       val values: List<Outcome<TValue>>?) {


    fun next(nextOffset:TOffset, nextValues:List<Outcome<TValue>>?): PagedState<TOffset, TValue> {
        return this.copy(batch = batch + 1, offset = nextOffset, values = nextValues)
    }
}
