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
 * Smart String for United States Phone Number.
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
class PhoneUS(text: String, required: Boolean = true) : SmartString(text, required, 10, 14,
        listOf("""\d{10}""",
                """\d{11}""",
                """\d{3}[-]?\d{3}[-]?\d{4}""",
                """\d{1}[-]?\d{3}[-]?\d{3}[-]?\d{4}"""
        )) {
    override val name = "PhoneUS"
    override val desc = "United States Phone Format"
    override val examples = listOf("1234567890", "11234567890", "123-456-7890", "1-234-567-8901")
    override val formats = listOf("xxxxxxxxxx", "xxxxxxxxxxx", "xxx-xxx-xxxx", "x-xxx-xxx-xxxx")

    fun isDashed(): Boolean = !text.isNullOrEmpty() && text.contains("-")
}