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

package kiit.common.lex

import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try
import kiit.results.getOrElse

/**
 * Created by kreddy on 3/2/2016.
 */
open class Lexer(val text: String) {

    private val state = LexState(text)
    private val settings = LexSettings()

    fun parse(): LexResult {
        val res = getTokens()
        val tokens = res.getOrElse({ listOf<Token>() })
        return LexResult(res.success, res.desc.orEmpty(), tokens, tokens.size, false, null)
    }

    fun getTokens(batchSize: Int = -1): Try<List<Token>> {
        val result = try {
            val tokens = mutableListOf<Token>()
            while (state.pos < state.END && !isEndOfTokenBatch(batchSize, tokens.size)) {
                val c = state.text[state.pos]
                val charPos = state.charPos
                state.count = state.count + 1

                // CASE 1: space
                val token = if (c == ' ' || c == '\t') {
                    state.incrementPos()
                    Token.none
                }
                // CASE 2: identifier
                else if (c.isLetter()) {
                    val word = readWord()
                    buildWordToken(word, charPos, state.count)
                }
                // CASE 3: number
                else if (c.isDigit()) {
                    val word = readNumber()
                    Token(word, word.toDouble(), TokenType.Number, state.line, charPos, state.count)
                }
                // CASE 4: string
                else if (c == '"' || c == '\'') {
                    val text = readString()
                    Token(text, text, TokenType.String, state.line, charPos, state.count)
                }
                // CASE 5: comment #
                else if (c == '#') {
                    val line = readComment()
                    Token(line, line, TokenType.Comment, state.line, charPos, state.count)
                }
                // CASE 6: number with .04
                else if (c == '.' && peekChar().isDigit()) {
                    val word = readNumber()
                    Token(word, word.toDouble(), TokenType.Number, state.line, charPos, state.count)
                }
                // CASE 7: newline
                else if (c == '\r' || c == '\n') {
                    val line = readNewLine()
                    Token(line, line, TokenType.Comment, state.line, charPos, state.count)
                }
                // CASE 8: other
                else {
                    val word = state.text[state.pos]
                    state.incrementPos()
                    Token(word.toString(), word.toString(), TokenType.NonAlphaNum, state.line, charPos, state.count)
                }

                // Add to token
                if (token != Token.none) {
                    tokens.add(token)
                }
            }
            // At end of text
            if (state.pos >= state.END) {
                val endToken = Token("<END>", "<END>", TokenType.End, state.line, state.charPos, state.count)
                if (batchSize <= -1 || (batchSize > -1 && tokens.size < batchSize))
                    tokens.add(endToken)
            }
            Success(tokens.toList())
        } catch (ex: Exception) {
            val line = state.line
            val cpos = state.charPos
            val text = "Error occurred at : line : $line, char : $cpos"
            Failure(ex, msg = text)
        }
        return result
    }

    fun isEndOfTokenBatch(batchCount: Int, tokenCount: Int): Boolean {
        return batchCount > -1 && tokenCount >= batchCount
    }

    fun readWord(): String {
        val start = state.pos

        fun isValidWordChar(curr: Char): Boolean {
            val isFirstChar = start == state.pos
            val isValidNonChar = curr == '-' || curr == '_' || curr.isDigit()
            val isValidNonFirstChar = !isFirstChar && isValidNonChar

            val res = if (curr.isLetter())
                true
            else isValidNonFirstChar
            return res
        }
        return readUntil(state.pos, state.pos, false, false, { _, curr, _ -> !isValidWordChar(curr) })
    }

    /**
     * Reads a number token of only digits
     * @return
     */
    fun readNumber(): String {
        var hasHitPeriod = false
        return readUntil(state.pos, state.pos, false, false, { _, curr, next ->
            val isDigit = curr.isDigit()
            // Case 1: integer ( no "." yet )
            val isValid = if (!hasHitPeriod && isDigit) {
                true
            } else if (!hasHitPeriod && curr == '.' && next.isDigit()) {
                hasHitPeriod = true
                true
            } else if (hasHitPeriod && isDigit) {
                true
            } else {
                isDigit
            }
            !isValid
        })
    }

    /**
     * Reads a string token beginning with either single or double quotes ' ""
     * @return
     */
    fun readString(): String {
        val quote = state.text[state.pos]
        state.incrementPos()
        return readUntil(state.pos, state.pos, false, true, { prev, curr, _ -> prev != '\\' && curr == quote })
    }

    /**
     * Reads a  line or new \n or \r\n
     * @return
     */
    fun readNewLine(): String {
        val ch = state.text[state.pos]
        return if (ch == '\n') {
            state.incrementPos()
            "\n"
        } else {
            require(ch == '\r')
            state.incrementPos()
            val n1 = state.text[state.pos]
            require(n1 == '\n')
            state.incrementPos()
            "\r\n"
        }
    }

    /**
     * Reads a comment
     * @return
     */
    fun readComment(): String {
        state.incrementPos()
        return readUntil(state.pos, state.pos, false, false, { prev, curr, _ -> prev != '\\' && (curr == '\n' || curr == '\r') })
    }

    tailrec fun readUntil(
        startPos: Int,
        currentPos: Int,
        includeLastChar: Boolean,
        incrementAfterExtract: Boolean,
        untilPredicate: (Char, Char, Char) -> Boolean
    ): String {
        return if (state.pos >= state.END) {
            val text = state.substring(startPos)
            state.incrementPos()
            text
        } else {
            val prevPos = if (currentPos > 0) currentPos - 1 else 0
            val prev = state.text[prevPos]
            val curr = state.text[currentPos]
            val next = if (currentPos + 1 < state.END) state.text[currentPos + 1] else Char.MIN_SURROGATE
            val isEnded = untilPredicate(prev, curr, next)
            if (isEnded) {

                val text = if (includeLastChar) {
                    state.incrementPos()
                    state.substring(startPos)
                } else {
                    state.substring(startPos)
                }
                if (incrementAfterExtract) {
                    state.incrementPos()
                }
                text
            } else {
                state.incrementPos()
                readUntil(startPos, state.pos, includeLastChar, incrementAfterExtract, untilPredicate)
            }
        }
    }

    protected fun peekChar(): Char {
        val nextPos = state.pos + 1
        return if (nextPos < state.END)
            state.text[nextPos]
        else
            Character.MIN_VALUE
    }

    protected fun buildToken(text: String, value: Any, tokenType: Int): Token {
        state.count = state.count + 1
        val token = Token(text, value, tokenType, state.line, state.charPos, state.count)
        return token
    }

    protected fun buildWordToken(word: String, charPos: Int, ndx: Int): Token {
        val t: Token =
                if (!settings.enableBoolIdentifiers) {
                    Token(word, word, TokenType.Ident, state.line, charPos, ndx)
                }
                // Case 1: true
                else if (word == "true" || (settings.enableBoolYesNoIdentifiers && word == "yes")) {
                    Token(word, true, TokenType.Boolean, state.line, charPos, ndx)
                }
                // Case 2: false
                else if (word == "false" || (settings.enableBoolYesNoIdentifiers && word == "no")) {
                    Token(word, false, TokenType.Boolean, state.line, charPos, ndx)
                } else {
                    Token(word, word, TokenType.Ident, state.line, charPos, ndx)
                }
        return t
    }
}
