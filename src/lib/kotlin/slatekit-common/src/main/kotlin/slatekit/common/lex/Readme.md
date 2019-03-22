
# Lex

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Lexer for parsing text into tokens</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.common.lex</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-common</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/lex" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/lex</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Lexer.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Lexer.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.common.lex.Lexer
import slatekit.common.lex.Token
import slatekit.common.lex.TokenType


// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Try
import slatekit.results.Success




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


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
    

{{< /highlight >}}
{{% break %}}

