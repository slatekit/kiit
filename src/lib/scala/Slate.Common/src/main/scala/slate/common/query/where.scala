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

package slate.common.query.dsl

import slate.common.query.{IQuery, Op, Query}





object where
{
  def apply(field:String, op:Op, value:Any) : IQuery =
  {
    new Query().where(field, op.value, value)
  }
}


