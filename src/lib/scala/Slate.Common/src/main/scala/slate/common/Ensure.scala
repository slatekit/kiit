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

/**
  * Although Scala's Option[T] and SlateKits Result[T] are heavily used and functional
  * error handling approaches are recommended, there are times and use cases this
  * Guard ( Assert like class ) can be used for traditional and imperative exception handling.
  * This is especially useful to throw exceptions early such as during startup / initialization
  */
object Ensure {

  /**
    * Ensures condition is true, otherwise throws an IllegalArgumentException with the message
    * @param condition : The condition to check
    * @param message   : Message for the exception
    */
  def isTrue(condition:Boolean, message:String): Unit =
  {
    if(!condition)
    {
      throw new IllegalArgumentException(message)
    }
  }


  /**
    * Ensures condition is false, otherwise throws an IllegalArgumentException with the message
    * @param condition : The condition to check
    * @param message   : Message for the exception
    */
  def isFalse(condition:Boolean, message:String): Unit =
  {
    if(!condition)
    {
      throw new IllegalArgumentException(message)
    }
  }


  /**
    * Ensures text is not empty, otherwise throws an IllegalArgumentException with the message
    * @param text      : The string to check
    * @param message   : Message for the exception
    */
  def isNotEmptyText(text:String, message:String): Unit =
  {
    if(Strings.isNullOrEmpty(text))
    {
      throw new IllegalArgumentException(message)
    }
  }


  /**
    * Ensures text exists in the list, otherwise throws an IllegalArgumentException with the message
    * @param text      : The string to check for
    * @param items     : The list of items to search
    * @param message   : Message for the exception
    */
  def isOneOfSupplied(text:String, items:List[String], message:String):Unit = {
    if(!items.contains(text)){
      throw new IllegalArgumentException(message)
    }
  }


  /**
    * Ensures item is not null, otherwise throws an IllegalArgumentException with the message
    * @param item      : The value to check
    * @param message   : Message for the exception
    */
  def isNotNull(item:AnyRef, message:String): Unit =
  {
    if(item == null)
    {
      throw new IllegalArgumentException(message)
    }
  }


  /**
    * Ensures condition is true, otherwise throws an IllegalArgumentException with the message
    * @param pos       : The index position
    * @param size      : The size to check against
    * @param message   : Message for the exception
    */
  def isValidIndex(pos:Int, size:Int, message:String): Unit =
  {
    if(pos < 0 || pos >= size)
    {
      throw new IndexOutOfBoundsException(message)
    }
  }
}
