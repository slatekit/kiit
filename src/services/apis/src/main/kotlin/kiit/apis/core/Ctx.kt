/**
 <kiit_header>
url: www.slatekit.com
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package kiit.apis.core

import kiit.apis.ApiServer
import kiit.context.Context

data class Ctx(val host: ApiServer, val context: Context)
