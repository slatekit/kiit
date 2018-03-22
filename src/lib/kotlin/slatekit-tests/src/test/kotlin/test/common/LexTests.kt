/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.common


/**
 * Created by kishorereddy on 5/22/17.
 */

import org.junit.Test
import slatekit.common.lex.Lexer


class LexTests {


    @Test fun can_read_ident() {
        val lex = Lexer("val")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens[0].text == "val")
        assert(result.tokens.size == 2)
    }


    @Test fun can_read_number() {
        val lex = Lexer("30")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens[0].text == "30")
        assert(result.tokens.size == 2)
    }


    @Test fun can_read_string_via_single_quote() {
        val lex = Lexer("'bat'")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens[0].text == "bat")
        assert(result.tokens.size == 2)
    }


    @Test fun can_read_string_via_double_quote() {
        val lex = Lexer("\"batman\"")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens[0].text == "batman")
        assert(result.tokens.size == 2)
    }


    @Test fun can_read_newline_via_n() {
        val lex = Lexer("\n")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens[0].text == "\n")
        assert(result.tokens.size == 2)
    }


    @Test fun can_read_newline_via_rn() {
        val lex = Lexer("\r\n")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens[0].text == "\r\n")
        assert(result.tokens.size == 2)
    }


    @Test fun can_read_comment() {
        val lex = Lexer("#comment here")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens.size == 2)
        assert(result.tokens[0].text == "comment here")
    }


    @Test fun can_read_comment_with_newline() {
        val lex = Lexer("#comment here\r\n")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens.size == 3)
        assert(result.tokens[0].text == "comment here")
    }


    @Test fun can_read_all_types_at_once() {
        val lex = Lexer("val 123 'bat' \"man\" \n \r\n #comment")
        val result = lex.parse()
        assert(result.success)
        assert(result.tokens[0].text == "val")
        assert(result.tokens[1].text == "123")
        assert(result.tokens[2].text == "bat")
        assert(result.tokens[3].text == "man")
        assert(result.tokens[4].text == "\n")
        assert(result.tokens[5].text == "\r\n")
        assert(result.tokens[6].text == "comment")
        assert(result.tokens.size == 8)
    }
}