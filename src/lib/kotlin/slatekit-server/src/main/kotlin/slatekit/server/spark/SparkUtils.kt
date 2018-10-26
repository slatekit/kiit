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

package slatekit.server.spark

import spark.ResponseTransformer

class JsonTransformer : ResponseTransformer {
    override fun render(obj: Any): String {
        return ""
    }


    companion object {
        @JvmStatic
        fun json(): ResponseTransformer {
            return JsonTransformer()
        }
    }
}