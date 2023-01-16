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

package kiit.utils.templates

import kiit.common.lex.LexState
import kiit.common.utils.Loops
import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try

/**
 * Parses a text template that can contain variables/substitutions.
 * e.g. An email template such as :
 *
 * "Hi @{user.name}, Welcome to @{startup.name}, please click @{verifyUrl} to verify your email."
 *
 * Is parsed into individual Template parts
 * 1. "Hi"                    -> text
 * 2. "user.name"             -> substitution
 * 3. ", Welcome to "         -> text
 * 4. "startup.name"          -> substitution
 * 5. ", please click"        -> text
 * 6. "verifyUrl"             -> substitution
 * 7. " to verify your email" -> text
 *
 * @param text
 */
class TemplateParser(val text: String) {

    private val state = LexState(text)

    fun parse(): Try<List<TemplatePart>> {

        val subs = mutableListOf<TemplatePart>()
        val result =
                try {
                    var lastText: String = ""
                    var lastPos = state.pos

                    while (state.pos < state.END) {
                        val c = state.text[state.pos]
                        val hasMore = state.pos + 1 < state.END
                        val n = if (hasMore) state.text[state.pos + 1] else ' '

                        state.count = state.count + 1

                        // CASE 1: substitution
                        if (c == '@' && hasMore && n == '{') {
                            if (!lastText.isNullOrEmpty()) {
                                val sub = TemplatePart(lastText, TemplateConstants.TypeText, lastPos, state.pos - lastPos)
                                subs.add(sub)
                            }
                            val sub = readSub()
                            subs.add(sub)

                            // reset
                            lastText = ""
                            lastPos = state.pos
                        }
                        // CASE 2: Keep reading until sub
                        else {
                            lastText += c
                            state.pos += 1
                        }
                    }

                    // Last part was text ?!
                    if (!lastText.isNullOrEmpty()) {
                        val sub = TemplatePart(lastText, TemplateConstants.TypeText, lastPos, state.pos - lastPos)
                        subs.add(sub)
                    }

                    // Success!
                    Success(subs.toList())
                } catch (ex: Exception) {
                    val line = state.line
                    val text = "Error occurred at : line : $line, char : ${state.pos}" + ex.message
                    val err = Exception(text, ex)
                    Failure(err, msg = text)
                }
        return result
    }

    /**
     * reads a substitution inside of @{}
     * expects that pos/current char = @ and next char = {
     *
     * @return
     */
    fun readSub(): TemplatePart {

        advanceAndExpect('{')
        val c = advance()
        val start = state.pos
        val end = start
        // 1. edge case ${}
        return if (c == '}') {
            advance()
            TemplatePart("", 0, start, end)
        } else {
            // 2. read sub
            Loops.doUntil {
                if (state.pos < state.END) {
                    val curr = state.text[state.pos]
                    if (curr == '}') {
                        false
                    } else if ((state.pos + 1 < state.END)) {
                        state.pos += 1
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }

            val text = state.substringInclusive(start)
            val sub = TemplatePart(text, TemplateConstants.TypeSub, start, state.pos)
            state.pos += 1
            sub
        }
    }

    fun advanceAndExpect(expected: Char): Char {
        val ch = advance()
        require(ch == expected) { "Expected $expected but found $ch" }
        return ch
    }

    fun advance(): Char {
        state.pos += 1
        return state.text[state.pos]
    }
}
