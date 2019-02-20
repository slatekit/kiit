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

package slatekit.entities.support

import slatekit.common.Context
import slatekit.common.encrypt.EncryptSupport
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.LogSupport
import slatekit.common.log.Logger
import slatekit.entities.core.Entities
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityRepo
import slatekit.entities.core.EntityService
import slatekit.results.Notice

/**
 * Entity Service wrapper with support for encryption, logging, results, and application context
 * @param context
 * @param repo
 * @tparam T
 */
open class EntityServiceWithSupport<T>(val context: Context, val entities: Entities, repo: EntityRepo<T>)
    : EntityService<T>(entities, repo), EncryptSupport, LogSupport where T : Entity {

    override val logger: Logger? get() = context.logs.getLogger()
    override val encryptor: Encryptor? get() = context.enc

    protected fun <T> handleError(err: String): Notice<T> {
        logger?.error(err)
        return slatekit.results.Failure(err)
    }
}
