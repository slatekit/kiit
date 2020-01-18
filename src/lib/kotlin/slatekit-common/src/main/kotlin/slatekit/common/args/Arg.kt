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

package slatekit.common.args

import slatekit.common.console.*

/**
 *
 * @param alias : alias for the argument     ( -e   )
 * @param name : name of the argument       ( -env )
 * @param desc : description of argument
 * @param isRequired : whether arg is required
 * @param isCased : case sensitive
 * @param isDevOnly : used for development only
 * @param isInterpreted : if arg can be interpreted ( @date.today )
 * @param group : used to group args
 * @param tag : used to tag an arg
 * @param defaultVal : default value for the arg
 * @param example : example of an arg value
 * @param exampleMany : multiple examples of arg values
 */
data class Arg(
        val alias: String = "",
        val name: String = "",
        val desc: String = "",
        val dataType: String = "",
        val isRequired: Boolean = true,
        val isCased: Boolean = true,
        val isDevOnly: Boolean = false,
        val isInterpreted: Boolean = false,
        val group: String = "",
        val tag: String = "",
        val defaultVal: String = "",
        val example: String = "",
        val exampleMany: String = ""
) {

    /**
     * prints the arg for command line display
     *
     * -env     :  the environment to run in
     *             ! required  [String]  e.g. dev
     * -log     :  the log level for logging
     *             ? optional  [String]  e.g. info
     * -enc     :  whether encryption is on
     *             ? optional  [String]  e.g. false
     * -region  :  the region linked to app
     *             ? optional  [String]  e.g. us
     *
     * @param tab
     * @param prefix
     * @param separator
     * @param maxWidth
     */
    fun semantic(
            prefix: String? = "-",
            separator: String? = "=",
            maxWidth: Int? = null
    ): List<TextOutput> {

        val nameLen = maxWidth ?: name.length
        val nameFill = name.padEnd(nameLen)
        val namePart = (prefix ?: "-") + nameFill

        val logs = mutableListOf(
                TextOutput(TextType.Highlight, namePart, false),
                TextOutput(TextType.Text, separator ?: "=", false),
                TextOutput(TextType.Text, desc, true),
                TextOutput(TextType.Text, " ".repeat(nameLen + 6), false))

        if (isRequired) {
            logs.add(TextOutput(TextType.Important, "!", false))
            logs.add(TextOutput(TextType.Text, "required ", false))
        } else {
            logs.add(TextOutput(TextType.Success, "?", false))
            logs.add(TextOutput(TextType.Text, "optional ", false))
        }

        logs.add(TextOutput(TextType.Subtitle, "[$dataType] ", false))
        logs.add(TextOutput(TextType.Text, "e.g. $example", true))
        return logs.toList()
    }
}
