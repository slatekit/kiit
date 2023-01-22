package kiit.common

import kiit.common.envs.EnvMode
import kiit.common.ids.ULIDs

/**
 * Used to identity services / components
 * form = area.service.agent.env.instance
 * name = signup.alerts.job.qat
 * full = signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
 */
interface Identity {

    val id: String
    val name: String
    val fullname: String
    val idWithTags: String

    val area: String
    val service: String
    val agent: Agent
    val env: String
    val version: String
    val desc: String
    val instance: String
    val tags: List<String>

    fun newInstance(): Identity
    fun with(inst: String? = null, tags: List<String>): Identity

    companion object {

        val empty = SimpleIdentity("empty", "empty", Agent.Test, "empty")

        fun app(area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return SimpleIdentity(area, service, Agent.App, env.name)
        }

        fun api(area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return SimpleIdentity(area, service, Agent.API, env.name)
        }

        fun cli(area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return SimpleIdentity(area, service, Agent.CLI, env.name)
        }

        fun job(area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return SimpleIdentity(area, service, Agent.Job, env.name)
        }

        fun test(name: String): Identity {
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
    override val area: String,
    override val service: String,
    override val agent: Agent,
    override val env: String,
    override val instance: String = ULIDs.create().value,
    override val version: String = "LATEST",
    override val desc: String = "",
    override val tags: List<String> = listOf()
) : Identity {
    private val tagged = tags.joinToString()

    /**
     * Enforced naming convention for an application's name ( simple name )
     * {AREA}.{SERVICE}
     * @sample: signup.alerts
     */
    override val name = "$area.$service"

    /**
     * Enforced naming convention for application's full name with agent and env
     * {AREA}.{SERVICE}.{AGENT}.{ENV}
     * signup.alerts.job.qat
     */
    override val fullname: String =
        "$name.${agent.name.lowercase()}.${env.lowercase()}.${version.replace(".", "_")}"

    /**
     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
     *
     */
    override val id: String = "$fullname.$instance"

    /**
     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3.a1,b2,c3
     *
     */
    override val idWithTags: String = "$fullname.$instance" + if (tagged.isNullOrEmpty()) "" else ".$tagged"

    override fun newInstance(): Identity = this.copy(instance = ULIDs.create().value)
    override fun with(inst: String?, tags: List<String>): Identity =
        this.copy(instance = inst ?: ULIDs.create().value, tags = tags)
}
