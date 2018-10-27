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

package slatekit.common.lex

import slatekit.common.Failure
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.getOrElse

/**
 * Created by kreddy on 3/2/2016.
 */
open class Lexer(val text: String) {

    private val _state = LexState(text)
    private val _settings = LexSettings()

    fun parse(): LexResult {
        val res = getTokens()
        val tokens = res.getOrElse({ listOf<Token>() })
        return LexResult(res.success, res.msg.orEmpty(), tokens, tokens.size, false, null)
    }

    fun getTokens(batchSize: Int = -1): ResultEx<List<Token>> {
        val result = try {
            val tokens = mutableListOf<Token>()
            while (_state.pos < _state.END && !isEndOfTokenBatch(batchSize, tokens.size)) {
                val c = _state.text[_state.pos]
                val charPos = _state.charPos
                _state.count = _state.count + 1

                // CASE 1: space
                val token = if (c == ' ' || c == '\t') {
                    _state.incrementPos()
                    Token.none
                }
                // CASE 2: identifier
                else if (c.isLetter()) {
                    val word = readWord()
                    buildWordToken(word, charPos, _state.count)
                }
                // CASE 3: number
                else if (c.isDigit()) {
                    val word = readNumber()
                    Token(word, word.toDouble(), TokenType.Number, _state.line, charPos, _state.count)
                }
                // CASE 4: string
                else if (c == '"' || c == '\'') {
                    val text = readString()
                    Token(text, text, TokenType.String, _state.line, charPos, _state.count)
                }
                // CASE 5: comment #
                else if (c == '#') {
                    val line = readComment()
                    Token(line, line, TokenType.Comment, _state.line, charPos, _state.count)
                }
                // CASE 6: number with .04
                else if (c == '.' && peekChar().isDigit()) {
                    val word = readNumber()
                    Token(word, word.toDouble(), TokenType.Number, _state.line, charPos, _state.count)
                }
                // CASE 7: newline
                else if (c == '\r' || c == '\n') {
                    val line = readNewLine()
                    Token(line, line, TokenType.Comment, _state.line, charPos, _state.count)
                }
                // CASE 8: other
                else {
                    val word = _state.text[_state.pos]
                    _state.incrementPos()
                    Token(word.toString(), word.toString(), TokenType.NonAlphaNum, _state.line, charPos, _state.count)
                }

                // Add to token
                if (token != Token.none) {
                    tokens.add(token)
                }
            }
            // At end of text
            if (_state.pos >= _state.END) {
                val endToken = Token("<END>", "<END>", TokenType.End, _state.line, _state.charPos, _state.count)
                if (batchSize <= -1 || (batchSize > -1 && tokens.size < batchSize))
                    tokens.add(endToken)
            }
            Success(tokens.toList())
        } catch (ex: Exception) {
            val line = _state.line
            val cpos = _state.charPos
            val text = "Error occurred at : line : $line, char : $cpos"
            Failure(ex, msg = text)
        }
        return result
    }

    fun isEndOfTokenBatch(batchCount: Int, tokenCount: Int): Boolean {
        return batchCount > -1 && tokenCount >= batchCount
    }

    fun readWord(): String {
        val start = _state.pos

        fun isValidWordChar(curr: Char): Boolean {
            val isFirstChar = start == _state.pos
            val isValidNonChar = curr == '-' || curr == '_' || curr.isDigit()
            val isValidNonFirstChar = !isFirstChar && isValidNonChar

            val res = if (curr.isLetter())
                true
            else isValidNonFirstChar
            return res
        }
        return readUntil(_state.pos, _state.pos, false, false, { _, curr, _ -> !isValidWordChar(curr) })
    }

    /**
     * Reads a number token of only digits
     * @return
     */
    fun readNumber(): String {
        var hasHitPeriod = false
        return readUntil(_state.pos, _state.pos, false, false, { _, curr, next ->
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
        val quote = _state.text[_state.pos]
        _state.incrementPos()
        return readUntil(_state.pos, _state.pos, false, true, { prev, curr, _ -> prev != '\\' && curr == quote })
    }

    /**
     * Reads a  line or new \n or \r\n
     * @return
     */
    fun readNewLine(): String {
        val ch = _state.text[_state.pos]
        return if (ch == '\n') {
            _state.incrementPos()
            "\n"
        } else {
            require(ch == '\r')
            _state.incrementPos()
            val n1 = _state.text[_state.pos]
            require(n1 == '\n')
            _state.incrementPos()
            "\r\n"
        }
    }

    /**
     * Reads a comment
     * @return
     */
    fun readComment(): String {
        _state.incrementPos()
        return readUntil(_state.pos, _state.pos, false, false, { prev, curr, _ -> prev != '\\' && (curr == '\n' || curr == '\r') })
    }

    tailrec fun readUntil(
        startPos: Int,
        currentPos: Int,
        includeLastChar: Boolean,
        incrementAfterExtract: Boolean,
        untilPredicate: (Char, Char, Char) -> Boolean
    ): String {
        return if (_state.pos >= _state.END) {
            val text = _state.substring(startPos)
            _state.incrementPos()
            text
        } else {
            val prevPos = if (currentPos > 0) currentPos - 1 else 0
            val prev = _state.text[prevPos]
            val curr = _state.text[currentPos]
            val next = if (currentPos + 1 < _state.END) _state.text[currentPos + 1] else Char.MIN_SURROGATE
            val isEnded = untilPredicate(prev, curr, next)
            if (isEnded) {

                val text = if (includeLastChar) {
                    _state.incrementPos()
                    _state.substring(startPos)
                } else {
                    _state.substring(startPos)
                }
                if (incrementAfterExtract) {
                    _state.incrementPos()
                }
                text
            } else {
                _state.incrementPos()
                readUntil(startPos, _state.pos, includeLastChar, incrementAfterExtract, untilPredicate)
            }
        }
    }

    protected fun peekChar(): Char {
        val nextPos = _state.pos + 1
        return if (nextPos < _state.END)
            _state.text[nextPos]
        else
            Character.MIN_VALUE
    }

    protected fun buildToken(text: String, value: Any, tokenType: Int): Token {
        _state.count = _state.count + 1
        val token = Token(text, value, tokenType, _state.line, _state.charPos, _state.count)
        return token
    }

    protected fun buildWordToken(word: String, charPos: Int, ndx: Int): Token {
        val t: Token =
                if (!_settings.enableBoolIdentifiers) {
                    Token(word, word, TokenType.Ident, _state.line, charPos, ndx)
                }
                // Case 1: true
                else if (word == "true" || (_settings.enableBoolYesNoIdentifiers && word == "yes")) {
                    Token(word, true, TokenType.Boolean, _state.line, charPos, ndx)
                }
                // Case 2: false
                else if (word == "false" || (_settings.enableBoolYesNoIdentifiers && word == "no")) {
                    Token(word, false, TokenType.Boolean, _state.line, charPos, ndx)
                } else {
                    Token(word, word, TokenType.Ident, _state.line, charPos, ndx)
                }
        return t
    }
}
