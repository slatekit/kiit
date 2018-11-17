---
layout: start_page_mods_utils
title: module Lex
permalink: /kotlin-mod-lex
---

# Lex

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Lexer for parsing text into tokens | 
| **date**| 2018-11-16 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.lex  |
| **source core** | slatekit.common.lex.Lexer.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Lexer.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Lexer.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.lex.Lexer
import slatekit.common.lex.Token
import slatekit.common.lex.TokenType


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.ResultEx
import slatekit.common.Success



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


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
    

```

