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

package slatekit.core.cli

import slatekit.common.*
import slatekit.common.args.Args
import java.io.InputStream

/**
 * Represents a user command typed on the command line. Contains both the method and parameters.
 * Supported format is "area.name.action" -key=value * e.g.
 * app.users.activate -email=john@gmail.com -status="active"
 * NOTE: The "area.name.action" format makes the shell command compatible with the Api Routing
 * format of the API module ( to support protocol independent APIs )
 * @param area : The area in the method
 * @param name : The name in the method
 * @param action : The action in the method
 * @param line : The raw line of text supplied by user.
 * @param args : The arguments supplied.
 */
data class CliCommand(
    val area: String,
    val name: String,
    val action: String,
    val line: String,
    val args: Args,
    val result: Response<Any>? = null
) : RequestSupport {
    /**
     * the area, name and action combined.
     * @return
     */
    fun fullName(): String {
        return if (name.isNullOrEmpty())
            area
        else if (action.isNullOrEmpty())
            area + "." + name
        else
            area + "." + name + "." + action
    }

    /**
     * whether or not this matches the area, naem, action supplied.
     * @param area
     * @param name
     * @param action
     * @return
     */
    fun isAction(area: String, name: String, action: String): Boolean =
            this.area == area && this.name == name && this.action == action

    override fun raw(): Any? = this

    override fun getDoc(name: String): Doc {
        return Uris.readDoc(this.args.getString(name))!!
    }

    /**
     * Access to an file
     */
    override fun getFile(name: String, callback: (InputStream) -> Doc): Doc {
        return Uris.readDoc(this.args.getString(name))!!
    }

    /**
     * Access to an file stream
     */
    override fun getFileStream(name: String, callback: (InputStream) -> Unit) {
    }

    companion object {

        @JvmStatic
        fun build(args: Args, line: String): CliCommand {
            val area = args.getVerb(0)
            val name = args.getVerb(1)
            val action = args.getVerb(2)
            return CliCommand(area, name, action, line, args)
        }
    }
}
