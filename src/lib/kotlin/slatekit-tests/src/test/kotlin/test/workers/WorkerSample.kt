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
package test.workers

import slatekit.common.log.LogsDefault
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try
import slatekit.workers.Job
import slatekit.workers.Worker

class WorkerSample(name:String, group:String, desc:String, val batch:Int = 10)
    : Worker<String>(name, desc, group, "1.0", logs = LogsDefault ) {

    private var counter = 0


    override fun init(): Notice<Boolean> {
        println("${about.name}: initializing")
        return Success(true)
    }


    override fun perform(job: Job):Try<String> {
        counter++
        if(counter % batch == 0) {
            println("${about.name}: : handled: $counter")
        }
        return Success("Ok")
    }


    override fun end() {
        println("${about.name}: ending")
    }
}
