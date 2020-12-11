/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.integration.apis

import slatekit.actors.Feedback
import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.support.FileSupport
import slatekit.common.Identity
import slatekit.common.Sources
import slatekit.common.crypto.Encryptor
import slatekit.common.log.Logger
import slatekit.integration.common.AppEntContext
import slatekit.jobs.Job
import slatekit.jobs.Jobs
import slatekit.jobs.support.Command
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


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
