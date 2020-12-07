package slatekit.common

import slatekit.common.envs.EnvMode
import java.util.*

/**
 * Used to identity services / components
 * name = signup.alerts.job.qat
 * full = signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
 */
interface Identity {

    val id:String
    val name:String
    val fullname:String
    val idWithTags:String

    val area:String
    val service:String
    val agent: Agent
    val env :String
    val instance:String
    val tags:List<String>

    companion object {

        val empty = SimpleIdentity("empty", "empty", Agent.Test, "empty")

        fun app(area:String, service:String, env:EnvMode = EnvMode.Dev):Identity {
            return DetailIdentity(area, service, Agent.App, env.name)
        }

        fun api(area:String, service:String, env:EnvMode = EnvMode.Dev):Identity {
            return DetailIdentity(area, service, Agent.API, env.name)
        }

        fun cli(area:String, service:String, env:EnvMode = EnvMode.Dev):Identity {
            return DetailIdentity(area, service, Agent.CLI, env.name)
        }

        fun job(area:String, service:String, env:EnvMode = EnvMode.Dev):Identity {
            return DetailIdentity(area, service, Agent.Job, env.name)
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
        override val instance:String = UUID.randomUUID().toString(),
        override val tags:List<String> = listOf()) : Identity {
    private val tagged = tags.joinToString()

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

    /**
     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3.a1,b2,c3
     *
     */
    override val idWithTags:String = "$fullname.$instance" + if (tagged.isNullOrEmpty()) "" else ".$tagged"

    fun newInstance():SimpleIdentity = this.copy(instance = UUID.randomUUID().toString())
}


data class DetailIdentity(
        override val area:String,
        override val service:String,
        override val agent: Agent,
        override val env:String,
        override val instance:String = UUID.randomUUID().toString(),
        override val tags:List<String> = listOf(),
        val desc: String = "",
        val alias: String = "",
        val version: String = "1_0") : Identity {

    val tagged = tags.joinToString()

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
    override val id:String = "$fullname.$instance"/**

     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3.a1,b2,c3
     *
     */
    override val idWithTags:String = "$fullname.$instance" + if (tagged.isNullOrEmpty()) "" else ".$tagged"

    fun newInstance():DetailIdentity = this.copy(instance = UUID.randomUUID().toString())
}


