package kiit.common

import kiit.common.envs.EnvMode
import kiit.common.ext.toIdent
import kiit.common.ids.ULIDs

/**
 * Identity used to identity services / components
 *
 * form = company.area.service.agent.env.version.instance
 * name = app1.accounts.signup.alerts.job.qat
 * vers = app1.accounts.signup.alerts.job.qat.1_0_2_3
 * full = app1.accounts.signup.alerts.job.qat.1_0_2_3.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
 *
 * @param company : app1  | jetbrains   - company name/origin
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
data class Identity(
    val company: String,
    val area: String,
    val service: String,
    val agent: Agent,
    val env: String,
    val instance: String = ULIDs.create().value,
    val version: String = "LATEST",
    val desc: String = "",
    val tags: List<String> = listOf()
)  {
    private val tagged = tags.joinToString()

    /**
     * Enforced naming convention for an application's name ( simple name )
     * {COMPANY}.{AREA}.{SERVICE}.{AGENT}
     * @sample: app1.signup.alerts.job
     */
    val name = "$company.$area.$service.${agent.name}"

    /**
     * Enforced naming convention for application's full name with agent and env
     * {COMPANY}.{AREA}.{SERVICE}.{AGENT}.{ENV}.{VERSION}
     * app1.signup.alerts.job.qat.
     */
    val full: String =
        "$name.${env.lowercase()}.${version.replace(".", "_")}"

    /**
     * The id contains the instance name
     * @sample: app1.signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3
     *
     */
    val id: String = "$full.$instance"

    /**
     * The id contains the instance name
     * @sample: signup.alerts.job.qat.4a3b300b-d0ac-4776-8a9c-31aa75e412b3.a1,b2,c3
     *
     */
    val idWithTags: String = "$full.$instance" + if (tagged.isEmpty()) "" else ".$tagged"

    fun newInstance(): Identity = this.copy(instance = ULIDs.create().value)

    fun with(inst: String?, tags: List<String>): Identity =
        this.copy(instance = inst ?: ULIDs.create().value, tags = tags)


    companion object {

        val empty = Identity("", "empty", "empty", Agent.Test, "empty")

        fun app(company:String, area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return of(company, area, service, Agent.App, env)
        }

        fun api(company:String, area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return of(company, area, service, Agent.API, env)
        }

        fun cli(company:String, area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return of(company, area, service, Agent.CLI, env)
        }

        fun job(company:String, area: String, service: String, env: EnvMode = EnvMode.Dev): Identity {
            return of(company, area, service, Agent.Job, env)
        }

        fun test(company:String, name: String): Identity {
            return of(company, "tests", name, Agent.Test, EnvMode.Dev)
        }

        fun of(company:String, area: String, service: String, agent:Agent, env: EnvMode = EnvMode.Dev): Identity {
            return Identity(
                company.toIdent(),
                area.toIdent(),
                service.toIdent(),
                agent,
                env.name)
        }
    }
}
