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

package slate.core.cloud

import slate.common.queues.{QueueSource, QueueSourceMsg}

/**
  * Abstraction for cloud based message queue storage and retrieval
  */
abstract class CloudQueueBase extends QueueSource with CloudActions with QueueSourceMsg {


}
