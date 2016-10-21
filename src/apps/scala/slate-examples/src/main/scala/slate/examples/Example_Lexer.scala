/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slate.examples

//<doc:import_required>
import slate.common.lex.{Token, Tokens, TokenType, Lexer}
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
import slate.common.results.{ResultSupportIn}
//</doc:import_examples>


class Example_Lexer  extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    val lexer = new Lexer()

    // CASE 1: Get all the tokens at once
    val result = lexer.parse("-env:dev -text:'hello word' -batch:10 ")
    println("tokens: " + result.total)
    println("first: " + result.tokens(0))

    // CASE 2: Token definition
    // Tokens are created/parsed with fields:
    // - raw text parsed
    // - value ( converted from raw text )
    // - token type
    // - line #
    // - char #
    // - index
    val token = new Token("env", "env", TokenType.Ident, 1, 0, 1)
    println(token)

    // CASE 3: Tokens list
    // Coming soon. Tokens can be iterated over with assertions
    //</doc:examples>

    ok()
  }
}
