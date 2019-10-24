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
import slatekit.apis.setup.AuthModes
import slatekit.apis.setup.Protocols
import slatekit.apis.setup.Verbs
import slatekit.apis.support.ApiFileSupport
import slatekit.common.CommonContext
import slatekit.common.Strings
import slatekit.core.cache.Cache
import slatekit.core.cache.CacheItem
import slatekit.core.cache.CacheSettings

@Api(area = "infra", name = "cache", desc = "api info about the application and host",
        auth = AuthModes.keyed, roles = "admin", verb = Verbs.Auto, protocol = Protocols.All)
class CacheApi(override val context: CommonContext) : ApiFileSupport {

    val cache: Cache = Cache(CacheSettings(50))

    @Action(desc = "gets the names of keys in the cache")
    fun keys(): List<String> {
        return cache.keys()
    }

    @Action(desc = "gets the size of the cache")
    fun size(): Int {
        return cache.size()
    }

    @Action(desc = "gets the details of a single cache item")
    fun get(key: String): CacheItem? {
        val item = cache.getEntry(key)
        val text = item?.text ?: ""
        val len = text.length
        val copy = if (len <= 1000) item else item?.copy(text = Strings.truncate(item?.text ?: "", 1000))
        return copy
    }

    @Action(desc = "invalidates a single cache item")
    fun invalidate(key: String) {
        return cache.invalidate(key)
    }

    @Action(desc = "invalidates the entire cache")
    fun invalidateAll() {
        return cache.invalidateAll()
    }
}
