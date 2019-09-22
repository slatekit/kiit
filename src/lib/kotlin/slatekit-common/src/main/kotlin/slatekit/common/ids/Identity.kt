package slatekit.common.ids

import java.util.*

interface Identity {
    val area:String
    val env :String
    val agent:String
    val uuid:String

    val name:String
    val fullName:String
}


/**
 * Simple Identity used to identity sources / components
 * @param area    : signup-alerts   - service/project/task
 * @param env     : dev   | qat | pro  - environment
 * @param agent   : user1 | job | svc  - to distinguish multiple agents/users
 * @param uuid    : UUID               - this provides uniqueness if using same type/actor/worker
 *
 * name = signup-alerts.qat.job
 * full = signup-alerts.qat.job.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
*/
data class SimpleIdentity(
        override val area:String,
        override val env:String,
        override val agent:String,
        override val uuid:String = UUID.randomUUID().toString()) : Identity {

    override val name = "$area.$env.$agent"
    override val fullName:String = "$$name.$uuid"
}