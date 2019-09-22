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
package slatekit.apis.middleware

import slatekit.common.metrics.Lasts
import slatekit.common.requests.Request
import slatekit.common.requests.Response

interface Tracked : Middleware {

    val lasts: Lasts<Request, Response<*>, Exception>
}
