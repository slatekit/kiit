package slatekit.common

import slatekit.common.envs.EnvMode
import java.util.*

/**
 * Used to identity services / components
 * name = signup.alerts.job.qat
 * full = signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
 */
interface Identity {
    val env :String
    val area:String
    val service:String
    val instance:String
    val agent: Agent
    val name:String
    val fullname:String
    val id:String

    companion object {

        fun cmd(name:String, env:EnvMode = EnvMode.Dev):Identity {
            val tokens = name.split(".")
            val area = if(tokens.size > 1) tokens[0] else ""
            val svc = if(tokens.size > 1) tokens[1] else tokens[0]
            return DetailIdentity(area, svc, Agent.Cmd, env.name)
        }

        fun test(name:String): Identity {
            return SimpleIdentity("tests", name, Agent.Test, EnvMode.Dev.name)
        }
    }
}


/**
 * Simple Identity used to identity services / components
 * @param area    : area  | dept | org  - logical group 
 * @param service : user1 | job  | svc  - to distinguish multiple agents/users
 * @param agent   : api   | app  | job  - environment
 * @param env     : dev   | qat  | pro  - environment
 * @param instance: UUID                - id of the instance for multiple instances of service
 *
 *      = area.service.agent.env.instance
 * name = signup.alerts.job.qat
 * full = signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
*/
data class SimpleIdentity(
        override val area:String,
        override val service:String,
        override val agent: Agent,
        override val env:String,
        override val instance:String = UUID.randomUUID().toString()) : Identity {

    /**
     * Enforced naming convention for an application's name ( simple name )
     * @sample: signup.alerts
     */
    override val name = "$area.$service"

    /**
     * Enforced naming convention for application's full name with agent and env
     */
    override val fullname: String = "$name.${agent.name.toLowerCase()}.${env.toLowerCase()}"

    /**
     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
     *
     */
    override val id:String = "$fullname.$instance"

    fun newInstance():SimpleIdentity = this.copy(instance = UUID.randomUUID().toString())
}


data class DetailIdentity(
        override val area:String,
        override val service:String,
        override val agent: Agent,
        override val env:String,
        override val instance:String = UUID.randomUUID().toString(),
        val desc: String = "",
        val alias: String = "",
        val version: String = "1_0") : Identity {

    /**
     * Enforced naming convention for an application's name ( simple name )
     * @sample: signup.alerts
     */
    override val name = "$area.$service"

    /**
     * Enforced naming convention for application's full name with agent and env
     */
    override val fullname: String = "$name.${agent.name.toLowerCase()}.${env.toLowerCase()}.$version"

    /**
     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
     *
     */
    override val id:String = "$fullname.$instance"

    fun newInstance():DetailIdentity = this.copy(instance = UUID.randomUUID().toString())
}


