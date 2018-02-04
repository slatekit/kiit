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
import slatekit.apis.support.ApiWithSupport
import slatekit.integration.common.AppEntContext
import slatekit.core.workers.*


@Api(area = "infra", name = "workers", desc = "api to get version information", roles = "ops", auth = "key-roles", verb = "*", protocol = "*")
class WorkersApi(val sys:System, override val context: AppEntContext) : ApiWithSupport {


    /**
     * pauses the system
     */
    @ApiAction(desc = "pauses the workers system", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun pause() = sys.pause()


    /**
     * pauses the system
     */
    @ApiAction(desc = "shuts down the workers system", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun complete() = sys.done()


    /**
     * resumes the system
     */
    @ApiAction(desc = "resumes the workers system", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun resume() = sys.resume()


    /**
     * stops the system
     */
    @ApiAction(desc = "stops the workers system", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun stop() = sys.stop()


    /**
     * starts the group
     */
    @ApiAction(desc = "starts the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun startGroup(group:String) = sys.start(group)


    /**
     * pauses the group
     */
    @ApiAction(desc = "pauses the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun pauseGroup(group:String) = sys.pause(group)


    /**
     * resumes the group
     */
    @ApiAction(desc = "resumes the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun resumeGroup(group:String) =  sys.resume(group)


    /**
     * stops the group
     */
    @ApiAction(desc = "stops the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun stopGroup(group:String) =  sys.stop(group)


    /**
     * starts the worker in the group supplied
     */
    @ApiAction(desc = "starts the worker in the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun startWorker(group:String, worker:String) = sys.start(group, worker)


    /**
     * pauses the worker in the group supplied
     */
    @ApiAction(desc = "pauses the worker in the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun pauseWorker(group:String, worker:String) = sys.pause(group, worker)


    /**
     * resumes the worker in the group supplied
     */
    @ApiAction(desc = "resumes the worker in the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun resumeWorker(group:String, worker:String) = sys.resume(group, worker)


    /**
     * stops the worker in the group supplied
     */
    @ApiAction(desc = "stops the worker in the group", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun stopWorker(group:String, worker:String) = sys.stop(group, worker)
}