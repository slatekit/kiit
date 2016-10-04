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

package slate.common


trait TypeChecks {

  def isType[T](item:Any):Boolean =
  {
    if(item == null) { return false }

    if(item.isInstanceOf[T]) { return true }

    false
  }


  def isTypeNot[T](item:Any):Boolean =
  {
    !isType[T](item)
  }
}
