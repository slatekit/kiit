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

package slatekit.core.push

import slatekit.common.TODO.IMPLEMENT
import slatekit.results.Failure
import slatekit.results.Notice

class MessageServiceIOS() : MessageServiceBase() {

  override fun send(msg: Message): Notice<Boolean> {
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
    IMPLEMENT("ASYNC", "Figure out an async Http library to use or look at Kotlin CoRoutines")
  }
}
