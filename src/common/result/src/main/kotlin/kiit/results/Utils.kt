/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A Kotlin Tool-Kit for Server + Android
 *  </kiit_header>
 */

package kiit.results

fun Int.isInSuccessRange(): Boolean = this in Codes.SUCCESS.code..Codes.QUEUED.code
fun Int.isFilteredOut(): Boolean = this == Codes.IGNORED.code
fun Int.isInBadRequestRange(): Boolean = this in Codes.BAD_REQUEST.code..Codes.UNAUTHORIZED.code
fun Int.isInFailureRange(): Boolean = this in Codes.ERRORED.code..Codes.UNEXPECTED.code
