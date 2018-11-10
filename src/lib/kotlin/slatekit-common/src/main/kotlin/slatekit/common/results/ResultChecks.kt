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

package slatekit.common.results


object ResultChecks {

    fun isSuccessRange(code:Int): Boolean = code in 200..299

    fun isFilteredOut(code:Int): Boolean = code == ResultCode.FILTERED

    fun isBadRequestRange(code:Int): Boolean = code in 400..499

    fun isFailureRange(code:Int): Boolean = code in 500..599
}
