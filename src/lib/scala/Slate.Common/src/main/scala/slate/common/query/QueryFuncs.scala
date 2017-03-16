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

package slate.common.query



object QueryFuncs {

  object Eq     extends Op( "=" )
  object Neq    extends Op( "<>" )
  object LessEq extends Op( "<=" )
  object MoreEq extends Op( ">=" )
  object Less   extends Op( "<"  )
  object More   extends Op( ">"  )
  object In     extends Op( "in" )


  def where(field:String, op:Op, value:Any) : IQuery =
  {
    new Query().where(field, op.value, value)
  }
}




