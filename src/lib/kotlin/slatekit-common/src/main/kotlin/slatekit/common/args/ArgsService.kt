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

import slatekit.common.*
import slatekit.common.lex.Lexer
import slatekit.common.results.ResultFuncs.successOrError

/**
 * Parses arguments.
 */
class ArgsService {

    /**
     * Parses the arguments using the supplied prefix and separator for the args.
     * e.g. users.activate -email:kishore@gmail.com -code:1234
     *
     * @param line : the raw line of text to parse into {action} {key/value}* {position}*
     * @param prefix : the prefix for a named key/value pair e.g. "-" as in -env=dev
     * @param sep : the separator for a nmaed key/value pair e.g. "=" as in -env=dev
     * @param hasAction: whether the line of text has an action before any named args.
     *                   e.g. name.action {namedarg}*
     * @return
     */
    fun parse(
        line: String,
        prefix: String = "-",
        sep: String = "=",
        hasAction: Boolean = false,
        metaChar: String = "@",
        sysChar: String = "$"
    ): ResultEx<Args> {
        // Check 1: Empty line ?
        return if (line.isEmpty()) {
            Success(Args("", listOf<String>(), "", listOf<String>(), prefix, sep,
                    null, null, null, null, null))
        } else {
            // Check 2: Parse the line into words/args
            val lexer = Lexer(line)
            val result = lexer.parse()
            if (!result.success) {
                Failure(Exception(result.message), msg = result.message)
            } else {
                // Get the text from the tokens except for the last token(end token)
                val args = result.tokens.map { t -> t.text }.take(result.tokens.size - 1)
                val err = "Error parsing arguments"

                // Any text ?
                if (args.isEmpty()) {
                    Failure(Exception("No data provided"), msg = err)
                } else {
                    // Now parse the lexically parsed text into arguments
                    val parseResult = parseInternal(line, args, prefix, sep, hasAction, metaChar, sysChar)
                    parseResult
                }
            }
        }
    }

    private fun parseInternal(
        line: String,
        tokens: List<String>,
        prefix: String,
        sep: String,
        hasAction: Boolean,
        metaChar: String,
        sysChar: String
    ): ResultEx<Args> {
        return successOrError(
                {
                    // if input = "area.api.action -arg1="1" -arg2="2"
                    // result = "area.api.action"
                    val result = if (hasAction) {
                        val actionResult = ArgsFuncs.parseAction(tokens, prefix, metaChar, sysChar)
                        // Start of named args is always 1 after the action
                        val startOfNamedArgs = if (actionResult.pos == 0) 0 else actionResult.posLast
                        Triple(actionResult.action, actionResult.actions, startOfNamedArgs)
                    } else
                        Triple("", listOf<String>(), 0)

                    // action= "area.api.action" e.g. "app.users.activate"
                    val action = result.first

                    // e.g. ["area", "api", "action"]
                    val verbs = result.second

                    // index after the action where the named arguments begin.
                    val startOfNamedArgs = result.third

                    // Check for args
                    val argsResult = if (startOfNamedArgs >= tokens.size - 1)
                        ParsedArgs(mapOf<String, String>(), mapOf<String, String>(), mapOf<String, String>(), startOfNamedArgs)
                    else
                        ArgsFuncs.parseNamedArgs(tokens, startOfNamedArgs, prefix, sep, metaChar, sysChar)

                    // start of index args is always 1 after the named args
                    val startOfIndexArgs =
                            if (argsResult.ndx == startOfNamedArgs) startOfNamedArgs
                            else argsResult.ndx + 1

                    val indexResult: List<String> =
                            if (tokens.isNotEmpty() && startOfIndexArgs >= 0 && startOfIndexArgs <= (tokens.size - 1)) {
                                tokens.subList(argsResult.ndx, tokens.size)
                            } else
                                listOf<String>()

                    val args = Args(line, tokens, action, verbs.toList(), prefix, sep,
                            argsResult.named, argsResult.meta, argsResult.sys, indexResult, null)
                    args
                })
    }
}
