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

package slatekit.common.templates

import slatekit.common.*
import slatekit.common.lex.LexState
import slatekit.common.templates.TemplateConstants.TypeSub
import slatekit.common.templates.TemplateConstants.TypeText
import slatekit.common.utils.Loops

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

    private val _state = LexState(text)

    fun parse(): ResultEx<List<TemplatePart>> {

        val subs = mutableListOf<TemplatePart>()
        val result =
                try {
                    var lastText: String = ""
                    var lastPos = _state.pos

                    while (_state.pos < _state.END) {
                        val c = _state.text[_state.pos]
                        val hasMore = _state.pos + 1 < _state.END
                        val n = if (hasMore) _state.text[_state.pos + 1] else ' '

                        _state.count = _state.count + 1

                        // CASE 1: substitution
                        if (c == '@' && hasMore && n == '{') {
                            if (!lastText.isNullOrEmpty()) {
                                val sub = TemplatePart(lastText, TypeText, lastPos, _state.pos - lastPos)
                                subs.add(sub)
                            }
                            val sub = readSub()
                            subs.add(sub)

                            // reset
                            lastText = ""
                            lastPos = _state.pos
                        }
                        // CASE 2: Keep reading until sub
                        else {
                            lastText += c
                            _state.pos += 1
                        }
                    }

                    // Last part was text ?!
                    if (!lastText.isNullOrEmpty()) {
                        val sub = TemplatePart(lastText, TemplateConstants.TypeText, lastPos, _state.pos - lastPos)
                        subs.add(sub)
                    }

                    // Success!
                    Success(subs.toList())
                } catch (ex: Exception) {
                    val line = _state.line
                    val text = "Error occurred at : line : $line, char : ${_state.pos}" + ex.message
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
        val start = _state.pos
        val end = start
        // 1. edge case ${}
        return if (c == '}') {
            advance()
            TemplatePart("", 0, start, end)
        } else {
            // 2. read sub
            Loops.doUntil({
                if (_state.pos < _state.END) {
                    val curr = _state.text[_state.pos]
                    if (curr == '}') {
                        false
                    } else if ((_state.pos + 1 < _state.END)) {
                        _state.pos += 1
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            })

            val text = _state.substringInclusive(start)
            val sub = TemplatePart(text, TypeSub, start, _state.pos)
            _state.pos += 1
            sub
        }
    }

    fun advanceAndExpect(expected: Char): Char {
        val ch = advance()
        require(ch == expected, { "Expected $expected but found $ch" })
        return ch
    }

    fun advance(): Char {
        _state.pos += 1
        return _state.text[_state.pos]
    }
}
