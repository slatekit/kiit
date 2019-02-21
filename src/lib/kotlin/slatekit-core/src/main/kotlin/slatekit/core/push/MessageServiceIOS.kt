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

import slatekit.common.TODO
import slatekit.results.Failure
import slatekit.results.Notice
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MessageServiceIOS(executor: ExecutorService? = null) : MessageServiceBase() {

  // TODO.IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")
  private val exec = executor ?: Executors.newSingleThreadExecutor()

  override fun send(msg: Message): Notice<Boolean> {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    return Failure("Not implemented")
  }

  /**
   * Sends the message asynchronously
   *
   * @param msg : message to send
   * @return
   * @note : implement in derived class that can actually send the message
   */
  override fun sendAsync(msg: Message, callback:(Notice<Boolean>) -> Unit) {
    TODO.IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")

  }
}
