package slatekit.core.loader

import slatekit.common.DateTime
import slatekit.common.ext.format

/**
 * Represents the request to create a batch for load testing.
 * @param env : resource name ( could be a AWS SQS queue, database name
 * @param name : name for the request:
 * @param count : number of records to produce
 */
data class SampleRequest(val env: String, val name: String, val count: Int, val args: Map<String, Any>) {

    /**
     * Used for date prefix
     */
    val time: DateTime = DateTime.now()

    /**
     * [Date]-[Env]-[Count]-[Name]
     * 20180718-DEV-200-sample1
     */
    val uniqueName: String = """${time.format("YYYYMMdd")}-$env-$count-$name"""
}
