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
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiWithSupport
import slatekit.integration.common.AppEntContext
import slatekit.workers.*

@Api(area = "infra", name = "workers", desc = "api to get version information",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class WorkersApi(override val context: AppEntContext) : ApiWithSupport {

    private var sys: System? = null

    fun configure(sys:System){
        this.sys = sys
    }

    /**
     * starts the system
     */
    @ApiAction(desc = "start the workers system")
    fun start() = perform { it.start() }

    /**
     * pauses the system
     */
    @ApiAction(desc = "pauses the workers system")
    fun pause() = perform { it.pause() }

    /**
     * resumes the system
     */
    @ApiAction(desc = "resumes the workers system")
    fun resume() = perform { it.resume() }

    /**
     * stops the system
     */
    @ApiAction(desc = "stops the workers system")
    fun stop() = perform { it.stop() }

    /**
     * pauses the system
     */
    @ApiAction(desc = "shuts down the workers system")
    fun complete() = perform { it.done() }

    /**
     * starts the worker in the group supplied
     */
    @ApiAction(desc = "starts the worker")
    fun startWorker(worker: String) = perform { it.startWorker(worker) }

    /**
     * pauses the worker in the group supplied
     */
    @ApiAction(desc = "pauses the worker")
    fun pauseWorker(worker: String) = perform { it.pauseWorker(worker) }

    /**
     * resumes the worker in the group supplied
     */
    @ApiAction(desc = "resumes the worker")
    fun resumeWorker(worker: String) = perform { it.resumeWorker(worker) }

    /**
     * stops the worker in the group supplied
     */
    @ApiAction(desc = "stops the worker")
    fun stopWorker(worker: String) = perform { it.stopWorker(worker) }

    /**
     * Get the worker names
     */
    @ApiAction(desc = "gets the names of all the workers")
    fun getWorkerNames() = perform { it.getWorkerNames() }

    /**
     * Get the worker names
     */
    @ApiAction(desc = "gets the worker stats")
    fun getWorkerStats() = perform { it.getWorkerStats() }


    private fun perform(operation:(System) -> Unit) {
        if(sys == null) {
            error("Work System has not been configured")
            return
        }
        sys?.let { operation(it) }
    }
}
