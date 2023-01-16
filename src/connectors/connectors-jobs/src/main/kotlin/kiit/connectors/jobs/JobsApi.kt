/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
  *  </kiit_header>
 */

package kiit.connectors.jobs

import kiit.actors.pause.Feedback
import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.apis.support.FileSupport
import kiit.common.Sources
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import kiit.connectors.entities.AppEntContext
import kiit.jobs.Jobs
import kiit.results.Outcome
import kiit.results.builders.Outcomes


@Api(area = "infra", name = "workers", desc = "api to get version information",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class JobsApi(override val context: AppEntContext) : FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    private lateinit var jobs: Jobs

    fun configure(jobs: Jobs){
        this.jobs = jobs
    }

    /**
     * starts the system
     */
    @Action(desc = "start the job")
    suspend fun start(name:String):Outcome<String> = jobs.start(name).outcome()

    /**
     * pauses the system
     */
    @Action(desc = "pauses the job")
    suspend fun pause(name:String):Outcome<String> =jobs.pause(name).outcome()

    /**
     * resumes the system
     */
    @Action(desc = "resumes job")
    suspend fun resume(name:String):Outcome<String> = jobs.resume(name).outcome()

    /**
     * stops the system
     */
    @Action(desc = "stops the job")
    suspend fun delay(name:String):Outcome<String> = jobs.delay(name).outcome()

    /**
     * stops the system
     */
    @Action(desc = "stops the job")
    suspend fun process(name:String):Outcome<String> = jobs.process(name).outcome()

    /**
     * stops the system
     */
    @Action(desc = "stops the job")
    suspend fun stop(name:String):Outcome<String> = jobs.stop(name).outcome()

    /**
     * Get the names of all jobs
     */
    @Action(desc = "gets the names of all the workers")
    fun jobs():List<String> = jobs.ids.map { it.id }

    /**
     * Get the names of all workers on a job
     */
    @Action(desc = "gets the names of all the workers")
    fun workers():List<String> = jobs.ids.mapNotNull {
        val workers = jobs.get(it.name)?.workers
        val ids = workers?.getIds()?.map { it.id }
        ids
    }.flatten()


    private fun Feedback.outcome():Outcome<String> = Outcomes.of(this.success, "", this.msg)
}
