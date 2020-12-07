/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.common.lex.Lexer
import slatekit.common.lex.Token
import slatekit.common.lex.TokenType
//</doc:import_required>

//<doc:import_examples>


import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>



class Example_Lexer : Command("lexer") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    val lexer = Lexer("-env:dev -text:'hello word' -batch:10 ")

    // CASE 1: Get all the tokens at once
    val result = lexer.parse()
    println("tokens:" + result.total)

    // Print all the tokens
    result.tokens.forEach{ printToken(it) }

    // Results:
    // pos:1 , line:0, type:NonAlphaNum, text:'-'
    // pos:2 , line:0, type:Ident      , text:'env'
    // pos:3 , line:0, type:NonAlphaNum, text:':'
    // pos:4 , line:0, type:Ident      , text:'dev'
    // pos:6 , line:0, type:NonAlphaNum, text:'-'
    // pos:7 , line:0, type:Ident      , text:'text'
    // pos:8 , line:0, type:NonAlphaNum, text:':'
    // pos:9 , line:0, type:String     , text:'hello word'
    // pos:11, line:0, type:NonAlphaNum, text:'-'
    // pos:12, line:0, type:Ident      , text:'batch'
    // pos:13, line:0, type:NonAlphaNum, text:':'
    // pos:14, line:0, type:Number     , text:'10'
    // pos:15, line:0, type:End        , text:'<END>'


    // CASE 2: Token definition
    // Tokens are created/parsed with fields:
    // - raw text parsed
    // - value ( converted from raw text )
    // - token type
    // - line #
    // - char #
    // - index
    val token = Token("env", "env", TokenType.Ident, 1, 0, 1)
    println(token)

    // CASE 3: Tokens list
    // Coming soon. Tokens can be iterated over with assertions
    //</doc:examples>

    return Success("")
  }


  fun printToken(token:Token):Unit {

    val tokenType = when(token.tType) {
      TokenType.Ident       -> "Ident      "
      TokenType.String      -> "String     "
      TokenType.Number      -> "Number     "
      TokenType.Boolean     -> "Boolean    "
      TokenType.NonAlphaNum -> "NonAlphaNum"
      TokenType.NewLine     -> "NewLine    "
      TokenType.End         -> "End        "
      else        -> "Other      "
    }
    println("pos:${token.index}, line:${token.line}, type:$tokenType, text:'${token.text}'")
  }
}
