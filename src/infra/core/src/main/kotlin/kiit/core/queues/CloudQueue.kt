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

package kiit.core.queues

import slatekit.common.Provider
import kiit.core.cloud.CloudSupport

/**
 * Abstraction for cloud based message queue storage and retrieval
 */
interface CloudQueue<T> : AsyncQueue<T>, CloudSupport, Provider
