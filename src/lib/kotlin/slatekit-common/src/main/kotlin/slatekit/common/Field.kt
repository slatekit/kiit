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

package slatekit.common

/**
 * Created by kishorereddy on 5/27/17.
 */

annotation class Field(val name: String = "",
                       val desc: String = "",
                       val required: Boolean = true,
                       val length: Int = 0,
                       val defaultVal: String = "",
                       val encrypt: Boolean = false,
                       val eg: String = "")