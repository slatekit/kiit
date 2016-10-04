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


import scala.collection.mutable.Map


class Models {

  private val _lookup = Map[String,Model]()


  def add(model:Model):Unit =
  {
    _lookup(model.name) = model
  }


  def remove( name:String): Unit =
  {
    _lookup -= name
  }


  def contains(name:String):Boolean =
  {
    _lookup.contains(name)
  }


  def get(name:String): Model =
  {
    _lookup(name)
  }
}
