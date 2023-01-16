/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
  *  </kiit_header>
 */

package kiit.integration.apis

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.apis.support.FileSupport
import kiit.cache.*
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import kiit.common.Sources
import kiit.context.Context
import kiit.results.Outcome
import kiit.results.builders.Outcomes

@Api(area = "infra", name = "cache", desc = "api info about the application and host",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class CacheApi(override val context: Context, val caches: List<AsyncCache>) : FileSupport {

    private val lookup = caches.map { it.id.name to it }.toMap()
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
