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

/**
  * Created by kreddy on 3/2/2016.
  */
object TokenType {

  val None         = 0
  val Ident        = 1
  val String       = 2
  val Number       = 3
  val Boolean      = 4
  val NonAlphaNum  = 5
  val NewLine      = 6
  val End          = 7
  val ParamRef     = 8
  val Interpolated = 9
  val Comment      = 10
}
