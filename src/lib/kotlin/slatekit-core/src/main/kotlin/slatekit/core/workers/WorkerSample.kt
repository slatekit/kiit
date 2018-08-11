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

import slatekit.common.ResultEx
import slatekit.common.ResultMsg
import slatekit.common.Success
import slatekit.common.info.About

class WorkerSample(name:String, group:String, desc:String, val batch:Int = 10)
    : Worker<String>(name, desc, group, "1.0") {

    private var counter = 0


    override fun onInit(): ResultMsg<Boolean> {
        println("${about.name}: initializing")
        return Success(true)
    }


    override fun perform(job:Job):ResultEx<String> {
        counter++
        if(counter % batch == 0) {
            println("${about.name}: : handled: $counter")
        }
        return Success("Ok")
    }


    override fun onEnd() {
        println("${about.name}: ending")
    }
}
