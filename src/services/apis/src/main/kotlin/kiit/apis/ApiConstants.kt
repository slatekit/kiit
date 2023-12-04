/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.apis

/**
 * NOTE: The could be more type-safe but are needed as string for the annotations.
 *
 * These are only available here as constants to allow checking against
 * 1. Api's setup via annotations ( in which case they have to reference strings )
 * 2. Api's setup via configuration files ( again in which case they have to reference strings )
 */
object ApiConstants {
    const val version = "1.0"

    const val docKeyName = "doc-key"
    const val parent = "@parent"
    const val versionZero = "0"
}
