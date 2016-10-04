/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.common.results

import slate.common.{Result}

import scala.collection.immutable.LinearSeq

class ResultConverter(
                       val noData:(String) => Unit,
                       val text:(String) => Unit
                     ) {

  /**
   * prints the item checking for none,
   * @param result
   */
  def print(result:Result[Any]):Unit = {
    if (result.isEmpty) {
      empty()
      return
    }
    val data = result.get
    toItem(data)
    summary(result)
  }


  /**
   * prints an empty result
   */
  def empty(): Unit =
  {
    noData("no results/data")
  }


  /**
   * prints the result
   * @param result
   */
  def summary(result:Result[Any]):Unit = {

    // Stats.
    text("Success : " + result.success)
    text("Code    : " + result.code   )
    text("Message : " + result.msg    )
    text("Tag     : " + result.tag    )
    text("Ext     : " + result.ext    )
  }


  /**
   * prints the data for the result
   * @param data
   */
  def toItem(data:Any)
  {
    data match {
      case items: LinearSeq[Any] =>
        toList(items)
      case item: Option[Any] =>
        toAny(item)
      case _ =>
        toAny(Some(data))
    }
  }


  /**
   * prints a list of items
   * @param items
   */
  def toList(items:scala.collection.immutable.LinearSeq[Any]): Unit =
  {
    for(item <- items)
    {
      toItem(item)
    }
  }


  /**
   * prints an option of any
   * @param item
   */
  def toAny(item:Option[Any]): Unit =
  {
    if(item.isEmpty) return

    var obj = item.get

    if (obj.isInstanceOf[Option[Any]])
      obj = obj.asInstanceOf[Option[Any]].getOrElse("")

    text(obj.toString)
  }
}
