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

object Ioc {
  private val _lookup = Map[String, Any]()


  def register(key:String, obj:Any): Unit =
  {
    _lookup(key) = obj
  }


  def get(key:String):Option[Any] =
  {
    if(_lookup.contains(key))
      return Some(_lookup(key))
    None
  }
}
