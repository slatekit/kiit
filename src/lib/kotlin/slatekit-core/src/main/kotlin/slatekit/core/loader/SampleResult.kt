package slatekit.core.loader

import slatekit.common.DateTime

/**
 * Returned from the api call to generate sample load/batch.
 * This provides an indication of how long it took to add records to AWS Kinesis via KCL
 * @param request : reference to original request
 * @param start : start time of the sample
 * @param end : end time of the sample
 * @param duration ( in milliseconds )
 */
data class SampleResult(
    val request: SampleRequest,
    val start: DateTime,
    val end: DateTime,
    val duration: Long,
    val tag: String? = ""
)
