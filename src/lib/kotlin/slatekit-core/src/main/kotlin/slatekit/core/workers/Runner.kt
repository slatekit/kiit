/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.core.workers

import slatekit.common.status.RunStateComplete
import slatekit.common.status.RunStateIdle
import slatekit.common.status.RunStateRunning
import slatekit.core.workers.core.Utils
import java.util.concurrent.ThreadPoolExecutor



/**
 * Interface to handle execution of the background workers.
 * You can implement your own runner.
 */
interface Runner {
    fun execute(sys: System)
}


/**
 * Supports concurrent execution of background workers
 *
 * TODO: Incorporate some of the tips in this article later on.
 * @see: https://www.nurkiewicz.com/2014/11/executorservice-10-tips-and-tricks.html
 */
class DefaultRunner(sys:System) : Runner {

    private val registry = Registry(sys)
    private val manager = Manager(sys, registry)

    // # threads = # cores
    private val threads = Runtime.getRuntime().availableProcessors()
    // TODO: What should be a preferred queue size ?
    private val queueSize = threads * 3
    private val executor = Utils.newFixedThreadPoolWithQueueSize(threads, queueSize)
    private val threadPool = executor as ThreadPoolExecutor


    override fun execute(sys: System) {

        var state = sys.getState()

        // This could have been paused/stopped
        // and therefore could be resumed/started later.
        // RunStateComplete is the only state that allows
        // this code to keep going
        while(state != RunStateComplete) {

            // Same as not paused / stopped, so proceed to
            // run the workers.
            if(state == RunStateRunning ) {

                if(threadPool.queue.size < queueSize) {
                    executor.submit( {  manager.manage() } )
                }
            }

            // Enable pause ?
            if(sys.settings.pauseBetweenCycles) {
                Thread.sleep(sys.settings.pauseTimeInSeconds * 1000L)
            }
            state = sys.getState()
        }
    }
}
