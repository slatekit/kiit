package slatekit.common.paged

import slatekit.results.Outcome


/**
 * Paging support for a large data set/source
 */
interface Paged<TOffset, TValue>  {

    /**
     *
     */
    val source:String


    /**
     * The current batch number
     * @return
     */
    fun batch() :Long


    /**
     * The current offset in the data source ( e.g. primary key )
     * @return
     */
    fun offset():TOffset


    /**
     * Gets the next page.
     * There could be no more items, so it returns an Option
     * The items are mapped to TValue, the mapping could fail, so it returns an Result[TValue, Err]
     * @return
     */
    fun next(): List<Outcome<TValue>>?


    /**
     * Gets the next page using explicitly supplied offset
     * There could be no more items, so it returns an Option
     * The items are mapped to TValue, the mapping could fail, so it returns an Result[TValue, Err]
     * @param offset
     * @return
     */
    fun next(offset:TOffset, batchSize:Int): List<Outcome<TValue>>?
}