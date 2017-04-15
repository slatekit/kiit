---
layout: start_page_mods_utils
title: module Lex
permalink: /mod-lex
---

# Lex

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Lexer for parsing text into tokens | 
| **date**| 2017-04-12T22:59:14.730 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.lex  |
| **source core** | slate.common.lex.Lexer.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/lex](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/lex)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Lexer.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Lexer.scala) |
| **depends on** |   |

## Import
```scala 
// required 

import slate.common.Result
import slate.common.lex.{Token, TokenType, Lexer}


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


    val lexer = new Lexer("-env:dev -text:'hello word' -batch:10 ")

    // CASE 1: Get all the tokens at once
    val result = lexer.parse()
    println("tokens:" + result.total)

    // Print all the tokens
    result.tokens.foreach(printToken)

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
    val token = new Token("env", "env", TokenType.Ident, 1, 0, 1)
    println(token)

    // CASE 3: Tokens list
    // Coming soon. Tokens can be iterated over with assertions
    

```

