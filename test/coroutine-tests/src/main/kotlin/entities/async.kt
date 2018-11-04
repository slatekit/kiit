package entities

import kotlinx.coroutines.*

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

    val scope:CoroutineScope

    fun <W> async(block: suspend CoroutineScope.() -> W): Future<W> {

        //block: suspend CoroutineScope.() -> T
        return scope.async {
            block()
        }
    }


    suspend fun <W> await(call: () -> Future<W> ):W = call().await()
}
