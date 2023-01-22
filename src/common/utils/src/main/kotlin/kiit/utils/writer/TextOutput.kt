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

package kiit.utils.writer

/**
 * Created by kishorereddy on 5/19/17.
 */

data class TextOutput(val textType: TextType, val msg: String, val endLine: Boolean = false, val format: Boolean = true)
