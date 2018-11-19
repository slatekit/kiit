package slatekit.common.diagnostics

import slatekit.common.DateTime

/**
 *  name  ,  shard  ,   type,    vendor,  status ,   successes, failures,  success rate, error rate,   updated
*   db-app,  usa-est,   db   ,   rds   ,  success,   5000     , 5       ,  99.99       , .001      ,   11-20-2018T4:00:00PM
*   files1,  usa-est,   files,   s3    ,  success,   5000     , 5       ,  99.99       , .001      ,   11-20-2018T4:00:00PM
*   queue1,  usa-est,   queue,   sql   ,  success,   5000     , 5       ,  99.99       , .001      ,   11-20-2018T4:00:00PM
*   sms   ,  usa-est,   sms  ,   twilio,  success,   5000     , 75      ,  99.99       , .05       ,   11-20-2018T4:00:00PM
*   email ,  usa-est,   email,   sendgr,  success,   5000     , 75      ,  99.99       , .05       ,   11-20-2018T4:00:00PM

 */
open class Status(val name: String,
                  val shard: String,
                  val type: String,
                  val vendor: String,
                  val threshold: Double,
                  val status: Boolean,
                  val successes: Long,
                  val failures: Long,
                  val successRate: Double,
                  val failureRate: Double,
                  val updated: DateTime
)