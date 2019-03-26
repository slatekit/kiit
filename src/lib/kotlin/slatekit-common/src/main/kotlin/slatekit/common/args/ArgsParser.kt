package slatekit.common.args

import slatekit.common.utils.Loops
import slatekit.common.utils.Loops.repeatWithIndex

class ArgsParser(val prefix: String,
                 val sep: String,
                 val metaChar: String,
                 val sysChar: String) {

    val namedArgs = mutableMapOf<String, String>()
    val metaArgs = mutableMapOf<String, String>()
    val sysArgs = mutableMapOf<String, String>()


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
    fun parseAction(args: List<String>, prefix: String, prefixMeta: String, prefixSys: String): ParsedItem {
        // Get the first index of arg prefix ( e.g. "-" or "/"
        val indexPrefix = args.indexOfFirst { it == prefix || it == prefixMeta || it == prefixSys }

        // Get index after action "app.users.activate"
        val indexLast = if (indexPrefix < 0) args.size else indexPrefix

        // Get all the words until last index
        val actions = args.subList(0, indexLast)
                .filter { text ->
                    !text.isNullOrEmpty() &&
                            (text.trim() == "?" || text.matches(Regex("^[a-zA-Z0-9]*$")))
                }

        val action = actions.joinToString(".")
        return ParsedItem(action, actions, actions.size, indexLast)
    }


    /**
     * parses all the named args using prefix and separator e.g. -env=dev
     *
     * @param args
     * @param startIndex
     * @return
     */
    fun parseNamed(args: List<String>, startIndex: Int): ParsedArgs {

        // Parses all named args e..g -a=1 -b=2
        // Keep looping until the index where the named args ends
        // NOTE: Get the index after the last named arg.

        val endIndex = repeatWithIndex(startIndex, args.size) { ndx ->

            // GIVEN: -a=1 the tokens are : ( "-", "a", "=", "1" )
            val text = args[ndx]

            // e.g. "-a=1" Prefix ? "-"
            val nextIndex = if (isPrefix(text)) {

                // Case 1: Alias "--l" ( move next )
                if(isAliased(text, ndx, args)) {
                    // Move past double "-"
                    ndx + 1
                    extract(ndx + 1, text, args)
                }
                // Case 2: Normal key/value pair
                else {
                    extract(ndx, text, args)
                }
            } else
                ndx + 1

            // Next index could be the end of the list indicating to stop loop
            nextIndex
        }

        return ParsedArgs(namedArgs.toMap(), metaArgs, sysArgs, endIndex)
    }


    /**
     * Support 3 types: "named" - | "meta" @ | "system" $
     */
    private fun isPrefix(text:String):Boolean {
        return text == prefix || text == metaChar || text == sysChar
    }


    /**
     * Checks for another prefix in the next position ( used to check for aliases )
     */
    private fun isAliased(prefix:String, ndx:Int, args:List<String>):Boolean {
        val nextIndex = ndx + 1
        return nextIndex < args.size && args[nextIndex] == prefix
    }


    /**
     * Apply the parsed key/value pairs into the appropriate argument containters
     * e.g. "named" - | "meta" @ | "system" $
     */
    private fun apply(text:String, kvp:Triple<String, String, Int>){
        when (text) {
            prefix   -> namedArgs[kvp.first] = kvp.second
            metaChar -> metaArgs[kvp.first] = kvp.second
            else     -> sysArgs[kvp.first] = kvp.second
        }
    }


    /**
     * Extracts the key value pair and return the next index position
     */
    private fun extract(ndx:Int, text:String, args:List<String>):Int  {
        // Get "a" "1" from ( "a", "=", "1" )
        val keyValuePair = parseKeyValue(ndx, args)

        val endIndex: Int = keyValuePair?.let { kvp ->

            apply(text, kvp)

            kvp.third
        } ?: args.size
        return endIndex
    }


    private fun parseKeyValue(ndx: Int, args: List<String>): Triple<String, String, Int>? {

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
            val posEndKey = Loops.repeatWithIndexAndBool(posKeyExt, args.size) { posNext ->
                if (args[posNext] == ".") {
                    keyBuff.append("." + args[posNext + 1])
                    Pair(true, posNext + 2)
                } else {
                    Pair(false, posNext)
                }
            }

            // Move past "="
            val posVal = posEndKey + 1

            // Get value
            val value = args[posVal]

            // Now get key/value
            val key = keyBuff.toString()

            // Move past value
            val end = posVal + 1

            Triple(key, value, end)
        } else
            null
    }
}