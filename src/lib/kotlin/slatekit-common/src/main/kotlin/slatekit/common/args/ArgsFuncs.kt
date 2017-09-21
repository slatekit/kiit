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

import slatekit.common.Loops.repeatWithIndex
import slatekit.common.Loops.repeatWithIndexAndBool


object ArgsFuncs {

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
        }
        else {
            val arg = positional[pos]
            possibleMatches.toList().fold(false, { isMatch, text ->
                val res = if (text == arg) {
                    true
                }
                else if ("-" + text == arg) {
                    true
                }
                else if ("--" + text == arg) {
                    true
                }
                else if ("/" + text == arg) {
                    true
                }
                else
                    isMatch
                res
            })
        }
    }


    /**
     * parses the action from the command line args
     * e.g. ["app", ".", "users", ".", "activate", "-", "id", "=", "2" ]
     * the action would be "app.users.activate"
     *
     * @param args
     * @param prefix
     * @return ( action, actions, actionCount, end index )
     *         ( "app.users.activate", ["app", 'users", "activate" ], 3, 5 )
     */
    fun parseAction(args: List<String>, prefix: String, prefixMeta:String): ParsedItem {
        // Get the first index of arg prefix ( e.g. "-" or "/"
        val indexPrefix = args.indexOfFirst { it == prefix || it == prefixMeta }

        // Get index after action "app.users.activate"
        val indexLast = if (indexPrefix < 0) args.size else indexPrefix

        // Get all the words until last index
        val actions = args.subList(0, indexLast)
                .filter { text ->
                    !text.isNullOrEmpty()
                            && (text.trim() == "?" || text.matches(Regex("^[a-zA-Z0-9]*$")))
                }

        val action = actions.joinToString(".")
        return ParsedItem(action, actions, actions.size, indexLast)
    }


    /**
     * parses all the named args using prefix and separator e.g. -env=dev
     *
     * @param args
     * @param startIndex
     * @param prefix
     * @param sep
     * @return
     */
    fun parseNamedArgs(args: List<String>, startIndex: Int, prefix: String, sep: String, metaChar: String)
            : Triple<Map<String, String>, Map<String,String>, Int> {

        val namedArgs = mutableMapOf<String, String>()
        val metaArgs   = mutableMapOf<String, String>()

        // Parses all named args e..g -a=1 -b=2
        // Keep looping until the index where the named args ends
        // NOTE: Get the index after the last named arg.
        val endIndex = repeatWithIndex(startIndex, args.size, { ndx ->

            // GIVEN: -a=1 the tokens are : ( "-", "a", "=", "1" )
            val text = args[ndx]

            // e.g. "-a=1" Prefix ? "-"
            val nextIndex = if (text == prefix || text == metaChar) {

                // Get "a" "1" from ( "a", "=", "1" )
                val keyValuePair = parseKeyValuePair(ndx, args)

                val advance: Int = keyValuePair?.let { kvp ->

                    if(text == prefix) {
                        namedArgs[kvp.first] = kvp.second
                    }
                    else {
                        metaArgs[kvp.first] = kvp.second
                    }
                    kvp.third
                } ?: args.size

                // The index position after the named arg
                advance
            }
            else
                ndx + 1

            nextIndex
        })

        return Triple(namedArgs.toMap(), metaArgs, endIndex)
    }


    fun parseKeyValuePair(ndx: Int, args: List<String>): Triple<String, String, Int>? {

        // example: -a=1
        // prefix: "-", key: "a", sep: "=", value="1"
        val pos = ndx
        return if (pos + 3 < args.size) {

            // Move past "-"
            val posKey = pos + 1

            // Build the key e.g. "log" or "log.level"
            val keyBuff = StringBuilder(args[posKey])

            // Move to next part of key
            val posKeyExt = posKey + 1

            // Keep building key until "." is done
            val posEndKey = repeatWithIndexAndBool(posKeyExt, args.size, { posNext ->
                if (args[posNext] == ".") {
                    keyBuff.append("." + args[posNext + 1])
                    Pair(true, posNext + 2)
                }
                else {
                    Pair(false, posNext)
                }
            })

            // Move past "="
            val posVal = posEndKey + 1

            // Get value
            val value = args[posVal]

            // Now get key/value
            val key = keyBuff.toString()

            // Move past value
            val end = posVal + 1

            Triple(key, value, end)
        }
        else
            null
    }
}
