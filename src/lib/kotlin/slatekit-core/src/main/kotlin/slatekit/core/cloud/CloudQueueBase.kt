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

package slatekit.core.cloud

import slatekit.common.queues.QueueSource
import slatekit.common.queues.QueueSourceMsg


/**
 * Abstraction for cloud based message queue storage and retrieval
 */
abstract class CloudQueueBase : QueueSource, CloudActions, QueueSourceMsg {
}
