/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slatekit.core.push

import slatekit.common.Failure
import slatekit.common.ResultMsg
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


class MessageServiceIOS(executor: ExecutorService? = null) : MessageServiceBase(){

  // TODO.IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")
  private val exec = executor ?: Executors.newSingleThreadExecutor()


  override fun send(msg: Message): ResultMsg<Boolean> {
    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    return Failure("Not implemented")
  }


  /**
   * Sends the message asynchronously
   *
   * @param msg : message to send
   * @return
   * @note      : implement in derived class that can actually send the message
   */
  override fun sendAsync(msg: Message): Future<ResultMsg<Boolean>> {
    TODO.IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")
    return exec.submit(Callable {
      send(msg)
    })
  }
}
