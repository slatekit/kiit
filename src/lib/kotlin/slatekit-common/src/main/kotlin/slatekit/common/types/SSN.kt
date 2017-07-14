/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.types

import slatekit.common.SmartString


/**
 * Smart String for Social security number
 *
 *
 * NOTE: This should be used sparingly at the "boundary" or "entry-point"
 * of an application and/or function. Such as :
 *
 * 1. API endpoints ( to validate simple parameters coming in )
 * 2. Deserialization of a single value
 *
 *
 * USES:
 * This is used in Slate Kit to :
 * 1. Auto-validate parameters for APIs on the CLI (command-line)
 * 2. Auto-validate parameters for APIs on the Web (HTTP Endpoints)
 * 3. Replace strings parameters for functions at the boundary/entry-points for an app/feature.
 *
 *
 * WARNING:
 * While convenient, this is a "heavy" String replacement.
 * Do not use as a replacement for strings in general and
 * especially as a property for small objects
 */
class SSN(text:String, required:Boolean = true): SmartString(text, required, 9, 11,
        listOf( """\d{9}""",
                """\d{3}[-]?\d{2}[-]?\d{4}"""
        ))
{
    override val name = "SSN"
    override val desc = "Social Security Number"
    override val examples = listOf("123456789", "123-45-6789")
    override val formats  = listOf("xxxxxxxxx", "xxx-xx-xxxx")

    fun isDashed():Boolean = !text.isNullOrEmpty() && text.contains("-")
}