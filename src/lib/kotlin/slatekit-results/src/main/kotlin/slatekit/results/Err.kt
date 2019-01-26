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
package slatekit.results


/**
 * Enriches the code interface by :
 * 1. explicitly marks a code as Error subtype
 * 2. adds an optional exception field for extra information
 *
 * NOTE: This can be used to model errors using a hybrid code, message + exception
 */
interface Err : Code