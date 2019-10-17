package slatekit.common

import slatekit.common.envs.EnvMode
import java.util.*

interface Identity {
    val env :String
    val area:String
    val service:String
    val instance:String
    val agent: Agent
    val name:String
    val id:String

    companion object {
        fun test(name:String): Identity {
            return SimpleIdentity("tests", name, EnvMode.Dev.name, Agent.Test)
        }
    }
}


/**
 * Simple Identity used to identity sources / components
 * @param area    : area  | dept | org  - logical group 
 * @param service : user1 | job  | svc  - to distinguish multiple agents/users
 * @param env     : dev   | qat  | pro  - environment
 * @param instance: UUID                - id of the instance for multiple instances of service
 *
 * name = signup.alerts.job.qat
 * full = signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
*/
data class SimpleIdentity(
        override val area:String,
        override val service:String,
        override val env:String,
        override val agent: Agent,
        override val instance:String = UUID.randomUUID().toString()) : Identity {

    /**
     * Enforced naming convention for an application's identity
     * @sample: signup.alerts.job.qat
     */
    override val name = "$area.$service.${agent.name.toLowerCase()}.$env"


    /**
     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
     *
     */
    override val id:String = "$$name.$instance"
}


