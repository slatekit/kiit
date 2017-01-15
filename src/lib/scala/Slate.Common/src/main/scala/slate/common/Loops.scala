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

object Loops {

  /**
    * Looping structure to remove use of "vars" in other places.
    * @param count
    * @param condition
    */
  def repeat( count:Int, condition:(Int) => Boolean ):Unit = {
    var ndx = 0
    while(ndx < count ) {
      val success = condition(ndx)
      if(success)
        ndx += 1
      else
        ndx = count + 1
    }
  }


  /**
    * Looping structure to remove use of "vars" in other places.
    * @param condition
    */
  def forever( condition: => Boolean ):Unit = {
    until(condition)
  }


  /**
   * Looping structure to remove use of "vars" in other places.
   * @param condition
   */
  def until( condition: => Boolean ):Unit = {
    var process = true
    while(process ) {
      process = condition
    }
  }
}
