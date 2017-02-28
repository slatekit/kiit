/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.test

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.common.lex.Lexer
import slate.common.query.QueryEncoder

class LexerTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }


  describe ( "Reading single tokens") {

    it("can read ident") {
      val lex = new Lexer("val")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens(0).text == "val")
      assert(result.tokens.length == 2)
    }


    it("can read number") {
      val lex = new Lexer("30")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens(0).text == "30")
      assert(result.tokens.length == 2)
    }


    it("can read string via single quote") {
      val lex = new Lexer("'bat'")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens(0).text == "bat")
      assert(result.tokens.length == 2)
    }


    it("can read string via double quote") {
      val lex = new Lexer("\"batman\"")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens(0).text == "batman")
      assert(result.tokens.length == 2)
    }


    it("can read newline via n") {
      val lex = new Lexer("\n")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens(0).text == "\n")
      assert(result.tokens.length == 2)
    }


    it("can read newline via rn") {
      val lex = new Lexer("\r\n")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens(0).text == "\r\n")
      assert(result.tokens.length == 2)
    }


    it("can read comment") {
      val lex = new Lexer("#comment here")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens.length == 2)
      assert(result.tokens(0).text == "comment here")
    }


    it("can read comment with newline") {
      val lex = new Lexer("#comment here\r\n")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens.length == 3)
      assert(result.tokens(0).text == "comment here")
    }
  }


  describe ( "Reading multiple tokens") {

    it("can read all types at once") {
      val lex = new Lexer("val 123 'bat' \"man\" \n \r\n #comment")
      val result = lex.parse()
      assert(result.success)
      assert(result.tokens(0).text == "val")
      assert(result.tokens(1).text == "123")
      assert(result.tokens(2).text == "bat")
      assert(result.tokens(3).text == "man")
      assert(result.tokens(4).text == "\n")
      assert(result.tokens(5).text == "\r\n")
      assert(result.tokens(6).text == "comment")
      assert(result.tokens.length == 8)
    }
  }
}
