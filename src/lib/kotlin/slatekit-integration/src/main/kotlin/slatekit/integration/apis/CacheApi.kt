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

package slatekit.integration.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.support.FileSupport
import slatekit.cache.*
import slatekit.common.crypto.Encryptor
import slatekit.common.log.Logger
import slatekit.common.Sources
import slatekit.context.Context
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

@Api(area = "infra", name = "cache", desc = "api info about the application and host",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class CacheApi(override val context: Context, val caches: List<AsyncCache>) : FileSupport {

    private val lookup = caches.map { it.name to it }.toMap()
    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    @Action(desc = "gets the names of keys in the cache")
    suspend fun keys(name:String): List<String>? {
        return lookup[name]?.keys()
    }

    @Action(desc = "gets the size of the cache")
    suspend fun size(name:String): Int? {
        return lookup[name]?.size()
    }

    @Action(desc = "gets the details of a single cache item")
    suspend fun get(name:String, key: String): Any? {
        return lookup[name]?.getAsync<Any>(key)
    }

    @Action(desc = "gets the details of a single cache item")
    suspend fun stats(name:String): List<CacheStats>? {
        return lookup[name]?.stats()
    }

    @Action(desc = "invalidates a single cache item")
    suspend fun delete(name:String, key: String):Outcome<Boolean> {
        return operate(name) { it.delete(key) }
    }

    @Action(desc = "invalidates the entire cache")
    suspend fun deleteAll(name:String):Outcome<Boolean> {
        return operate(name) { it.deleteAll() }
    }

    @Action(desc = "invalidates a single cache item")
    suspend fun expire(name:String, key: String):Outcome<Boolean> {
        return operate(name) { it.expire(key) }
    }

    @Action(desc = "invalidates the entire cache")
    suspend fun expireAll(name:String):Outcome<Boolean> {
        return operate(name) { it.expireAll() }
    }

    private suspend fun <T> operate(key:String, op:suspend (AsyncCache) -> Outcome<T>): Outcome<T> {
        return when(val cache = lookup[key]) {
            null -> Outcomes.invalid("Cache with name $key not found")
            else -> op(cache)
        }
    }
}
