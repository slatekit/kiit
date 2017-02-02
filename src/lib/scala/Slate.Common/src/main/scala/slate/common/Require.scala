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

/**
  * Additional alternatives to scala require guard method.
  */
object Require {

  /**
    * Ensures text is not empty, otherwise throws an IllegalArgumentException with the message
    *
    * @param text      : The string to check
    * @param message   : Message for the exception
    */
  def requireText(text:String, message:String): Unit = {
    require(!Strings.isNullOrEmpty(text), message)
  }


  /**
    * Ensures text exists in the list, otherwise throws an IllegalArgumentException with the message
    *
    * @param text      : The string to check for
    * @param items     : The list of items to search
    * @param message   : Message for the exception
    */
  def requireOneOf(text:String, items:Seq[String], message: String): Unit = {
    require(items.contains(text, message))
  }


  /**
    * Ensures condition is true, otherwise throws an IllegalArgumentException with the message
    *
    * @param pos       : The index position
    * @param size      : The size to check against
    * @param message   : Message for the exception
    */
  def requireValidIndex(pos:Int, size:Int, message:String): Unit = {
    require(pos > 0 && pos < size, message)
  }
}
