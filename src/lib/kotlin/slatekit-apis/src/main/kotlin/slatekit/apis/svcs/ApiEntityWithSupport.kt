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

package slatekit.apis.svcs

import slatekit.apis.ApiBaseEntity
import slatekit.common.encrypt.EncryptSupport
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.LogSupport
import slatekit.common.log.LoggerBase
import slatekit.core.common.AppContext
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityService
import kotlin.reflect.KClass


open class ApiEntityWithSupport<T, TSvc>(
        context: AppContext, val type: KClass<*>
) : ApiBaseEntity<T>(context, type), EncryptSupport, LogSupport where T : Entity, TSvc : EntityService<T> {
    override val logger: LoggerBase? get() = context.log
    override val encryptor: Encryptor? get() = context.enc


    @Suppress("UNCHECKED_CAST")
    fun service(): TSvc = context.ent.getSvc<T>(type) as TSvc
}
