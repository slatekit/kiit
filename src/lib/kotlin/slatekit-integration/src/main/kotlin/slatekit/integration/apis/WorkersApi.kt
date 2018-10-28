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
import slatekit.common.Ioc
import slatekit.integration.common.AppEntContext
import slatekit.workers.*

@Api(area = "infra", name = "workers", desc = "api to get version information",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class WorkersApi(override val context: AppEntContext) : ApiWithSupport {

    val sys: System by lazy {
        Ioc.get<System>(System::class.qualifiedName ?: "")
    }

    /**
     * starts the system
     */
    @ApiAction(desc = "start the workers system")
    fun start() = sys.start()

    /**
     * pauses the system
     */
    @ApiAction(desc = "pauses the workers system")
    fun pause() = sys.pause()

    /**
     * resumes the system
     */
    @ApiAction(desc = "resumes the workers system")
    fun resume() = sys.resume()

    /**
     * stops the system
     */
    @ApiAction(desc = "stops the workers system")
    fun stop() = sys.stop()

    /**
     * pauses the system
     */
    @ApiAction(desc = "shuts down the workers system")
    fun complete() = sys.done()

    /**
     * starts the worker in the group supplied
     */
    @ApiAction(desc = "starts the worker")
    fun startWorker(worker: String) = sys.startWorker(worker)

    /**
     * pauses the worker in the group supplied
     */
    @ApiAction(desc = "pauses the worker")
    fun pauseWorker(worker: String) = sys.pauseWorker(worker)

    /**
     * resumes the worker in the group supplied
     */
    @ApiAction(desc = "resumes the worker")
    fun resumeWorker(worker: String) = sys.resumeWorker(worker)

    /**
     * stops the worker in the group supplied
     */
    @ApiAction(desc = "stops the worker")
    fun stopWorker(worker: String) = sys.stopWorker(worker)

    /**
     * Get the worker names
     */
    @ApiAction(desc = "gets the names of all the workers")
    fun getWorkerNames() = sys.getWorkerNames()

    /**
     * Get the worker names
     */
    @ApiAction(desc = "gets the worker stats")
    fun getWorkerStats() = sys.getWorkerStats()
}
