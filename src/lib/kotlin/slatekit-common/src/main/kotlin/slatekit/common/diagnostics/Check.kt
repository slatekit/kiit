package slatekit.common.diagnostics

import slatekit.common.DateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 *  name,    shard  ,   type,    vendor,  status ,   successes, failures,  error rate,   updated
    db-app,  usa-est,   db   ,   rds   ,  success,   5000     , 5       ,  .001      ,   11-20-2018T4:00:00PM
    files1,  usa-est,   files,   s3    ,  success,   5000     , 5       ,  .001      ,   11-20-2018T4:00:00PM
    queue1,  usa-est,   queue,   sql   ,  success,   5000     , 5       ,  .001      ,   11-20-2018T4:00:00PM
    sms   ,  usa-est,   sms  ,   twilio,  success,   5000     , 75      ,  .05       ,   11-20-2018T4:00:00PM
    email ,  usa-est,   email,   sendgr,  success,   5000     , 75      ,  .05       ,   11-20-2018T4:00:00PM

 */
open class Check(val name: String,
                 val shard:String,
                 val type:String,
                 val vendor:String,
                 val threshold:Double) {

    private val status = AtomicBoolean(true)
    private val successes = AtomicLong(0L)
    private val failures = AtomicLong(0L)
    private val successRate = AtomicReference(0.0)
    private val failureRate = AtomicReference(0.0)
    private val updated = AtomicReference(DateTime.now())


    /**
     * Calculates the status based on the updated success/failure rates
     * These should be coming from another source
     */
    fun calculate(successCount:Long, failureCount:Long) {
        successes.set(successCount)
        failures.set(failureCount)
        val total = successCount + failureCount
        val goodRate = successCount.toDouble() / total
        val badRate = failureCount.toDouble() / total
        val success = badRate < threshold
        successRate.set(goodRate)
        failureRate.set(badRate)
        updated.set(DateTime.now())
        status.set(success)
    }


    fun status(): Status {
        return Status(name, shard, type, vendor, threshold,
                status.get(), successes.get(), failures.get(), successRate.get(), failureRate.get(), updated.get())
    }
}
