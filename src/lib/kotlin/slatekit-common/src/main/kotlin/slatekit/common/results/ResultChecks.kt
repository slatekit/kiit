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


fun Int.isInSuccessRange(): Boolean = this in 200..299


fun Int.isFilteredOut(): Boolean = this == ResultCode.FILTERED


fun Int.isInBadRequestRange(): Boolean = this in 400..499


fun Int.isInFailureRange(): Boolean = this in 500..599
