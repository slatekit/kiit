/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.common.lex

data class LexSettings(
    val enableBoolIdentifiers: Boolean = false,
    val enableBoolYesNoIdentifiers: Boolean = false,
    val enableDashInIdentifiers: Boolean = false
)
