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
package slate.common

trait Reference {
  val value:String = ???
}


/**
  * Represents a reference to a specific field
 *
  * @param name
  * @param original
  */
case class RefField(name:String, original:String = "") extends Reference {
  override val value = original
}


/**
  * Represents a reference to a specific field
  *
  * @param name
  * @param original
  */
case class RefItem(id:String, name:String, original:String = "") extends Reference {
  override val value = original
}


/**
  * Represents a reference to a specific row/column field.
  * @param row
  * @param col
  * @param name
  * @param original
  */
case class RefCell(row:Int, col:Int, name:String, original:String = "") extends Reference {
  override val value = original
}


