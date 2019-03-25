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
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiWithSupport
import slatekit.common.CommonContext
import slatekit.common.Strings
import slatekit.core.cache.Cache
import slatekit.core.cache.CacheItem
import slatekit.core.cache.CacheSettings

@Api(area = "infra", name = "cache", desc = "api info about the application and host",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class CacheApi(override val context: CommonContext) : ApiWithSupport {

    val cache: Cache = Cache(CacheSettings(50))

    @ApiAction(desc = "gets the names of keys in the cache")
    fun keys(): List<String> {
        return cache.keys()
    }

    @ApiAction(desc = "gets the size of the cache")
    fun size(): Int {
        return cache.size()
    }

    @ApiAction(desc = "gets the details of a single cache item")
    fun get(key: String): CacheItem? {
        val item = cache.getEntry(key)
        val text = item?.text ?: ""
        val len = text.length
        val copy = if (len <= 1000) item else item?.copy(text = Strings.truncate(item?.text ?: "", 1000))
        return copy
    }

    @ApiAction(desc = "invalidates a single cache item")
    fun invalidate(key: String) {
        return cache.invalidate(key)
    }

    @ApiAction(desc = "invalidates the entire cache")
    fun invalidateAll() {
        return cache.invalidateAll()
    }
}
