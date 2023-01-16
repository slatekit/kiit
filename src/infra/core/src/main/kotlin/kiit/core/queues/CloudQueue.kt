/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
  *  </kiit_header>
 */

package kiit.core.queues

import kiit.common.Provider
import kiit.core.cloud.CloudSupport

/**
 * Abstraction for cloud based message queue storage and retrieval
 */
interface CloudQueue<T> : AsyncQueue<T>, CloudSupport, Provider
