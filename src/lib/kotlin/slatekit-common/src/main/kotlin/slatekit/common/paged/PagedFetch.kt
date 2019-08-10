package slatekit.common.paged

import slatekit.results.Outcome
import java.util.concurrent.atomic.AtomicReference


/**
 * Implementation of a full table scan(reading of all records) via paging using an offset ( e.g. primary key/partition key )
 *
 * @note Implementation is based on :
 * https://github.com/nblair/code-examples/blob/master/datastax-java-driver-examples/src/main/java/examples/datastax/FullTableScan.java
 * @param startOffset: Starting token to begin the paging
 *
 */
abstract class PagedFetch<TOffset, TValue>(override val source:String,
                                           val startOffset:TOffset,
                                           val batchSize:Int) : Paged<TOffset, TValue>  {
    protected val state = AtomicReference(PagedState<TOffset, TValue>(-1, startOffset, null))

    /**
     * Current offset e.g. ( primary/partition key of 1 millionth record from beginning )
     * @return
     */
    override fun offset(): TOffset = state.get().offset


    /**
     * Current batch number e.g. batch 2 where each batch handles 1000 records at a time
     * @return
     */
    override fun batch() : Long = state.get().batch


    /**
     * Gets the next page of records using current offset
     * @return
     */
    override fun next(): List<Outcome<TValue>>? {
        val curr = state.get()
        return next(curr.offset, batchSize)
    }


    /**
     * Gets the next page of records explicitly supplied offset
     * @return
     */
    override fun next(offset:TOffset,  batchSize:Int): List<Outcome<TValue>>? {
        // Fetch the next batch.
        // NOTE: This returns:
        // 1. offset of the last record
        // 2. list of mapped values to an Either[Err, TValue]
        // We have to return these values separately, because
        // we can not get the offset from the Either[Err, TValue] result
        val result = fetch(offset, batchSize)

        val nextState = state.get().next(result.first, result.second)
        state.set(nextState)
        return nextState.values
    }


    /**
     * Fetches the batch at the explicitly supplied offset
     * @param offset
     * @param batchSize
     * @return
     */
    abstract fun fetch(offset:TOffset, batchSize:Int): Pair<TOffset, List<Outcome<TValue>>?>
}