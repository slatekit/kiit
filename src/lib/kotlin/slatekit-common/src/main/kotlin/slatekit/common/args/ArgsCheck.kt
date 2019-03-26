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


object ArgsCheck {

    /**
     * returns true if there is only 1 argument with value: help ?
     *
     * @return
     */
    fun isHelp(items: List<String>, pos: Int) = isMetaArg(items, pos, "help", "?")

    /**
     * returns true if there is only 1 argument with value: version | ver
     *
     * @return
     */
    fun isVersion(items: List<String>, pos: Int) = isMetaArg(items, pos, "version", "ver")

    /**
     * returns true if there is only 1 argument with value: version | ver
     *
     * @return
     */
    fun isAbout(items: List<String>, pos: Int) = isMetaArg(items, pos, "about", "info")

    /**
     * returns true if there is only 1 argument with value: pause
     *
     * @return
     */
    fun isPause(items: List<String>, pos: Int) = isMetaArg(items, pos, "pause", "ver")

    /**
     * returns true if there is only 1 argument with value: --exit -quit /? -? ?
     *
     * @return
     */
    fun isExit(items: List<String>, pos: Int) = isMetaArg(items, pos, "exit", "quit")

    /**
     * checks for meta args ( e.g. request for help, version etc )
     * e..g
     * -help    |  --help     |  /help
     * -about   |  --about    |  /about
     * -version |  --version  |  /version
     *
     * @param positional
     * @param pos
     * @param possibleMatches
     * @return
     */
    fun isMetaArg(positional: List<String>, pos: Int, vararg possibleMatches: String): Boolean {
        val any = positional.isNotEmpty()
        val posOk = pos >= 0 && pos < positional.size

        return if (!any || !posOk) {
            false
        } else {
            val arg = positional[pos]
            possibleMatches.toList().fold(false) { isMatch, text ->
                when {
                    text == arg      -> true
                    "-$text" == arg  -> true
                    "--$text" == arg -> true
                    "/$text" == arg  -> true
                    else             -> isMatch
                }
            }
        }
    }
}
