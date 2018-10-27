package slatekit.core.loader

import slatekit.common.ResultMsg

interface SampleGenerator<T> where T : Sample {

    /**
     * Generates a default sample
     * @return
     */
    fun default(): ResultMsg<T>

    /**
     * Generates a sample with random values from existing set of data
     * such as existing user ids, etc.
     * @return
     */
    fun random(): ResultMsg<T>

    /**
     * Generates a sample event with the data supplied in the request.
     * This is assumed to be part of a batch of requests.
     * @param ndx : the index number of this request
     * @param batch: the batch size of this request
     * @return
     */
    fun create(request: SampleRequest, ndx: Int = 0, batch: Int): String
}
