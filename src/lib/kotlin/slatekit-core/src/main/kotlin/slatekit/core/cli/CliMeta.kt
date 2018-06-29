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
package slatekit.core.cli

import slatekit.common.Context

open class CliMeta {

    open fun getMetaData(context: Context, cmd:CliCommand, creds: slatekit.common.Credentials):Map<String, Any> {
        val keys = listOf( Pair("api-key", creds.key) )
        val meta = cmd.args.meta.plus(keys)
        return meta
    }
}
