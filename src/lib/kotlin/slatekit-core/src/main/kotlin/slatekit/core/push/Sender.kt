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

/**
 * @param id       : The id of the recipient
 * @param name     : The name of the recipient
 * @param platform : The platform of the user ios,
 * @param phone    : The phone number of the user ( for id when received on the client side )
 * @param device   : The device id of the recipient ( to physically send to the ios/google cloud etc )
 * @param args     : Extra arguments that can be supplied
 */
data class Sender(
    val id               : String       ,
    val name             : String       ,
    val platform         : Platform     ,
    val phone            : String   = "",
    val device           : String   = "",
    val args: Map<String,String>? = null
)
{
    companion object {
        val empty = Sender("", "", PlatformNone, "", "")
    }
}
