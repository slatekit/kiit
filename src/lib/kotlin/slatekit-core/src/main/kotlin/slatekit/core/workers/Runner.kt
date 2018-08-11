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
import slatekit.common.status.RunStateRunning

/**
 * Interface to handle execution of the background workers.
 * You can implement your own runner.
 */
interface Runner {
    fun execute(sys: System)
}



class DefaultRunner(sys:System) : Runner {

    val registry = Registry(sys)
    val manager = Manager(sys, registry)


    override fun execute(sys: System) {

        var state = sys.getState()

        // This could have been paused/stopped
        // and therefore could be resumed/started later.
        // RunStateComplete is the only state that allows
        // this code to keep going
        while(state != RunStateComplete) {

            // Same as not paused / stopped, so proceed to
            // run the workers.
            if(state == RunStateRunning) {
                manager.manage()
            }

            // Enable pause ?
            if(sys.settings.pauseBetweenCycles) {
                Thread.sleep(sys.settings.pauseTimeInSeconds * 1000L)
            }
            state = sys.getState()
        }
    }
}
