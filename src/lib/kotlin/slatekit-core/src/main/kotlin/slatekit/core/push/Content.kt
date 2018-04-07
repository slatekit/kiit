/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.core.push

import slatekit.common.DateTime


/**
 * The content is a fixed set of metadata specifically about the payload
 * and is actually separately stored and outside of the dynamic payload structure
 * This can be used for fast lookup / hints of the actual payload content
 */
data class Content(
    val title: String,
    val desc: String,
    val date: DateTime? = null,
    val args: Map<String,String>? = null
) {

    companion object {
        val empty = Content("", "")
    }
}
