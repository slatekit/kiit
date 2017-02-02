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

import scala.annotation.tailrec

object Loops {

  /**
    * "do while" loop alternative with index/count using tail recursion
    *
    * @param max
    * @param condition
    */
  def repeat( max:Int, condition:(Int) => Boolean ):Unit = {
    repeat(0, max, condition)
  }


  /**
    * "do while" loop alternative with index/count using tail recursion
    * @param ndx
    * @param max
    * @param condition
    */
  @tailrec
  def repeat(ndx:Int, max:Int, condition:(Int) => Boolean ): Unit = {
    if (ndx < max && condition(ndx))
      repeat(ndx + 1, max, condition)
  }


  /**
   * "do while" loop alternative
   *
   * @param condition
    */
  @tailrec
  def doUntil(condition: => Boolean ):Unit = {
    if(condition)
      doUntil(condition)
  }
}
