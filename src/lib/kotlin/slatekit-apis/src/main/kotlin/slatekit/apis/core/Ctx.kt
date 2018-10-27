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
package slatekit.apis.core

import slatekit.apis.ApiContainer
import slatekit.apis.ApiRef
import slatekit.common.Context
import slatekit.common.Request

data class Ctx(val container: ApiContainer, val context: Context, val req: Request, val apiRef: ApiRef)
