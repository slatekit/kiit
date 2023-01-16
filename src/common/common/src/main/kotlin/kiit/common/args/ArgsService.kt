/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.common.args

import kiit.common.lex.LexResult
import kiit.common.lex.Lexer
import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try
import kiit.results.builders.Tries
import kiit.results.then

/**
 * Parses arguments.
 */
object ArgsService {

    /**
     * Parses the arguments using the supplied prefix and separator for the args.
     * e.g. users.activate -email:kishore@gmail.com -code:1234
     *
     * @param text : the raw line of text to parse into {action} {key/value}* {position}*
     * @param prefix : the prefix for a named key/value pair e.g. "-" as in -env=dev
     * @param sep : the separator for a nmaed key/value pair e.g. "=" as in -env=dev
     * @param hasAction: whether the line of text has an action before any named args.
     *                   e.g. name.action {namedarg}*
     * @return
     */
    fun parse(
            text: String,
            prefix: String = Args.PREFIX,
            sep: String = Args.SEPARATOR,
            hasAction: Boolean = false,
            metaChar: String = Args.META_CHAR,
            sysChar: String = Args.SYS_CHAR
    ): Try<Args> {

         // NOTE: .then = flatMap
         return cleanse(text)
        .then { line:String         -> tokenize(line) }
        .then { result:LexResult    -> convert(result) }
        .then { tokens:List<String> -> process(text.trim(), tokens, prefix, sep, hasAction, metaChar, sysChar) }
    }

    /**
     * Cleanse the line before processing
     */
    private fun cleanse(line:String): Try<String> {
        val cleaned = line.trim()
        return Success(cleaned)
    }


    /**
     * Tokenize the line by parsing it lexically
     */
    private fun tokenize(line: String): Try<LexResult> {
        val result = Lexer(line).parse()
        return when (result.success) {
            false -> Failure(Exception(result.message), msg = result.message)
            true -> Success(result)
        }
    }


    /**
     * Validate the tokens
     */
    private fun convert(lexResult:LexResult): Try<List<String>> {
        val args = lexResult.tokens.map { t -> t.text }.take(lexResult.tokens.size - 1)
        return Success(args)
    }


    /**
     * Finally parse the tokens into arguments
     */
    private fun process(
            line: String,
            tokens: List<String>,
            prefix: String,
            sep: String,
            hasAction: Boolean,
            metaChar: String,
            sysChar: String
    ): Try<Args> {
        return Tries.of {

            // if input = "area.api.action -arg1="1" -arg2="2"
            // result = "area.api.action"
            val parser = ArgsParser(prefix, sep, metaChar, sysChar)
            val result = if (hasAction) {
                val actionResult = parser.parseAction(tokens, prefix, metaChar, sysChar)
                // Start of named args is always 1 after the action
                val startOfNamedArgs = if (actionResult.pos == 0) 0 else actionResult.posLast
                Triple(actionResult.action, actionResult.actions, startOfNamedArgs)
            } else
                Triple("", listOf(), 0)

            // action= "area.api.action" e.g. "app.users.activate"
            val action = result.first

            // e.g. ["area", "api", "action"]
            val verbs = result.second

            // index after the action where the named arguments begin.
            val startOfNamedArgs = result.third

            // Check for args
            val argsResult = if (startOfNamedArgs >= tokens.size - 1)
                ParsedArgs(mapOf(), mapOf(), mapOf(), startOfNamedArgs)
            else
                parser.parseNamed(tokens, startOfNamedArgs)

            // start of index args is always 1 after the named args
            val startOfIndexArgs =
                    if (argsResult.ndx == startOfNamedArgs) startOfNamedArgs
                    else argsResult.ndx + 1

            val indexResult: List<String> =
                    if (tokens.isNotEmpty() && startOfIndexArgs >= 0 && startOfIndexArgs <= (tokens.size - 1)) {
                        tokens.subList(argsResult.ndx, tokens.size)
                    } else
                        listOf()

            val args = Args(line, tokens, action, verbs.toList(), prefix, sep,
                    argsResult.named, argsResult.meta, argsResult.sys, indexResult, null)
            args
        }
    }
}
