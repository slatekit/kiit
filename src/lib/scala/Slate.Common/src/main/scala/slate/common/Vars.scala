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

package slate.common

import scala.collection.mutable.ListBuffer

class Vars(items:Option[List[(String,Any)]]) extends ListMap[String,Any]{

  init(items)


  private def init(items:Option[List[(String,Any)]]):Unit = {
    items.fold(Unit)( all => {
      all.foreach( item => {
        this.add(item._1, item._2)
      })
      Unit
    })
  }
}


object Vars {

  def apply(text:String): Vars = {
    val data = Strings.splitToMapWithPairs(text)
    val buf = ListBuffer[(String,Any)]()
    data.foreach( p => buf.append(( p._1, p._2.asInstanceOf[Any])))
    new Vars(Some(buf.toList))
  }

}
