package slatekit.async.futures

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future


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



interface AsyncExtensions {

    val service:ExecutorService


    fun <W> async(block: () -> W): Future<W> {
        return service.submit<W> {
            block()
        }
    }
}


fun <F> Future<F>.await():F = this.get()
