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

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.support.FileSupport
import slatekit.common.Identity
import slatekit.common.Sources
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logger
import slatekit.integration.common.AppEntContext
import slatekit.jobs.JobAction
import slatekit.jobs.Job

@Api(area = "infra", name = "workers", desc = "api to get version information",
        auth = AuthModes.Keyed, roles = ["admin"], verb = Verbs.Auto, sources = [Sources.All])
class JobsApi(override val context: AppEntContext) : FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    private lateinit var manager: Job

    fun configure(manager: Job){
        this.manager = manager
    }

    /**
     * starts the system
     */
    @Action(desc = "start the workers system")
    suspend fun start() = perform { it.request(JobAction.Start) }

    /**
     * pauses the system
     */
    @Action(desc = "pauses the workers system")
    suspend fun pause() = perform { it.request(JobAction.Pause) }

    /**
     * resumes the system
     */
    @Action(desc = "resumes the workers system")
    suspend fun resume() = perform { it.request(JobAction.Resume) }

    /**
     * stops the system
     */
    @Action(desc = "stops the workers system")
    suspend fun stop() = perform { it.request(JobAction.Stop) }

    /**
     * starts the worker in the group supplied
     */
    @Action(desc = "starts the worker")
    suspend fun startWorker(workerId: String) = requestWork(workerId){
        manager.request(JobAction.Start, it, "from api")
    }

    /**
     * pauses the worker in the group supplied
     */
    @Action(desc = "pauses the worker")
    suspend fun pauseWorker(workerId: String) = requestWork(workerId) {
        manager.request(JobAction.Pause, it, "from api")
    }

    /**
     * resumes the worker in the group supplied
     */
    @Action(desc = "resumes the worker")
    suspend fun resumeWorker(workerId: String) = requestWork(workerId) {
        manager.request(JobAction.Resume, it, "from api")
    }

    /**
     * stops the worker in the group supplied
     */
    @Action(desc = "stops the worker")
    suspend fun stopWorker(workerId: String) = requestWork(workerId) {
        manager.request(JobAction.Stop, it, "from api")
    }

    /**
     * Get the worker names
     */
    @Action(desc = "gets the names of all the workers")
    fun getWorkerNames():List<String> = manager.workers.getIds()


    private suspend fun perform(operation: suspend (Job) -> Unit) {
        if(this.manager == null) {
            error("Work System has not been configured")
            return
        }
        this.manager.let { operation(it) }
    }


    private suspend fun requestWork(workerId:String, operation: suspend (Identity) -> Unit){
        val worker = manager.workers.get(workerId)
        worker?.let {
            operation(worker.worker.id)
        }
    }
}
