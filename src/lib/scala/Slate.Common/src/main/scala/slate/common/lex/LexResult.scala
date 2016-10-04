/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.lex

import slate.common.Strings


/**
  * Result of lexical parsing. This could represent all the tokens or just a subset
  * @param success  : whether or not the lex parse was valid
  * @param message  : a message if there were errors
  * @param tokens   : the tokens parsed
  * @param total    : the total number of tokens parsed
  * @param isSubset : whether this represents a subset of tokens or all tokens
  * @param ex       : exception if failure
  */
case class LexResult(
                      success : Boolean      = false,
                      message : String       = null,
                      tokens  : List[Token] = null,
                      total   : Int          = 0,
                      isSubset: Boolean      = true,
                      ex      : Exception    = null
                    )
{
  def toStringDetail():String =
  {
    val text = s"$success, $message, total=$total, isSubset=$isSubset"
    var tokenDetail = ""
    for(token <- tokens)
    {
      tokenDetail += token.toStringDetail() + Strings.newline
    }
    text + Strings.newline + tokenDetail
  }
}
