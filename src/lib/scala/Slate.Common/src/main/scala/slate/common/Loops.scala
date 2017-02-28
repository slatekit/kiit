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

/**
 * alternatives to using do while loops and iterations where you want to stop 
 */
object Loops {

  /**
   * "do while" loop alternative using tailrec to avoid a "var" for checking conditions
   *
   * @param condition
   */
  @tailrec
  def doUntil(condition: => Boolean ):Unit = {
    // simulate do while with call by name ( evaluate first ) 
    if(condition)
      doUntil(condition)
  }


  /**
    * "do while" loop alternative with index/count using tail recursion
    *
    * @param max
    * @param condition
    */
  def doUntilIndex( max:Int, condition:(Int) => Boolean ):Unit = {
    
    @tailrec
    def rep(ndx:Int, max:Int, condition:(Int) => Boolean ):Unit = {
      if( ndx < max && condition(ndx) ){
        rep(ndx + 1, max, condition)
      }
    }
    
    // do : run first 
    if(max > 0) {
      val first = condition(0)
      if (first) {
        rep(1, max, condition)
      }
    }
  }


  /**
    * "takeWhile" iteration alternative.
    * this provides a way for the caller to dictate the next index.
    * 
    * NOTE: this is ideal for low-level character / string / lexical parsing  
    * @param condition
    */
  @tailrec
  def repeatWithIndex(ndx:Int, end:Int, condition: (Int) => Int ): Int = {
    val nextIndex = condition(ndx)
    if( nextIndex >= end)
      nextIndex
    else
      repeatWithIndex(nextIndex, end, condition)
  }


  /**
   * "takeWhile" iteration alternative.
   * this provides a way for the caller to dictate the next index along with a condition
   * to indicate continued traversal
   *
   * @param condition
   */
  @tailrec
  def repeatWithIndexAndBool(ndx:Int, end:Int, condition: (Int)=> (Boolean, Int) ): Int = {
    val result = condition(ndx)
    val success = result._1
    val nextIndex = result._2
    if(!success)
      ndx
    else
      repeatWithIndexAndBool(nextIndex, end, condition)
  }


  /**
   * "do while" loop alternative with index/count using tail recursion
   *
   * @param ndx
   * @param max
   * @param condition
   */
  @tailrec
  def repeatWithIndexResult[T](ndx:Int, max:Int, defautValue:Result[T], condition:(Int) => Result[T] ): Result[T] = {
    if(ndx >= max ){
      defautValue
    }
    else {
      val result = condition(ndx)
      if(!result.success)
        result
      else
        repeatWithIndexResult(ndx + 1, max, defautValue, condition)
    }
  }
}
