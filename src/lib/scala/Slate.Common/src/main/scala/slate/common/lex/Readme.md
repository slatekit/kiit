# Lex

| field | value  | 
|:--|:--|
| **desc** | Lexer for parsing text into tokens | 
| **date**| 2016-11-21T16:49:15.691 |
| **version** | 0.9.1  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.lex  |
| **source core** | slate.common.lex.Lexer.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/lex](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/lex)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Lexer.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Lexer.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.lex.{Token, Tokens, TokenType, Lexer}


// optional 
import slate.core.cmds.Cmd
import slate.common.results.{ResultSupportIn}


```

## Setup
```scala

n/a

```

## Usage
```scala


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
    

```

