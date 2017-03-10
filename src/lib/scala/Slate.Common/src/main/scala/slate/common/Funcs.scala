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

import slate.common.results.ResultFuncs._
import slate.common.results.ResultTimed

import scala.annotation.tailrec

object Funcs {

  /**
   * checks if there are any items in the list
   * null, 0 size yield false
   * @param items
   * @tparam T
   * @return
   */
  def any[T](items:Seq[T]):Boolean = Option(items).fold(false)( i => i.nonEmpty)


  /**
   * Gets a value in the list at the supplied position or returns the default value
   * @param items
   * @param pos
   * @param default
   * @tparam T
   * @return
   */
  def getListValueOrElse[T](items:Seq[T], pos:Int, default:T):T = {
    if(Option(items).isEmpty)
      default
    else if (pos < 0 || pos >= items.size)
      default
    else
      items(pos)
  }


  /**
   * Gets a value in the list at the supplied position or returns the default value
   * @param items
   * @param pos
   * @param default
   * @tparam T
   * @return
   */
  def getListValueOrElse[T](items:Option[Seq[T]], pos:Int, default:T):T = {
    items.fold[T]( default ) ( all => {
      if (pos < 0 || pos >= items.size)
        default
      else
        all(pos)
    })
  }


  /**
    * attempts to run callback inside try/catch
    * and returns a success, value from callback and optional exception
    *
    * @param callback
    * @return
    */
  def attempt[T](callback:() => T ): Result[T] = {
    val result =  try {
      val v = callback()
      success(v)
    }
    catch{
      case ex:Exception => {
        failure( err = Some(ex))
      }
    }
    result
  }


  /**
    * attempts to run callback inside try/catch
    * and returns a success, value from callback and optional exception
    *
    * @param callback
    * @return
    */
  def attemptTimed[T](callback:() => T ): ResultTimed[T] = {
    val started = DateTime.now()
    val result =  try {
      val v = callback()
      ResultTimed.build[T](started, success(v))
    }
    catch{
      case ex:Exception => {
        ResultTimed.build[T](started, failure( err = Some(ex)))
      }
    }
    result
  }


  /**
    * executes the code if all of the guards pass
    *
    * @param failureValue
    * @param guards
    * @param f
    * @tparam T
    * @return
    */
  def executeWithGuards[T](failureValue:T, guards:List[()=>Boolean], f: => T): T = {
    @tailrec
    def checkGuards(pos:Int, guards:List[()=>Boolean]):(Boolean, Int) = {
      if(pos < guards.size && !guards(pos)())
        (false, pos)
      else
        checkGuards(pos + 1, guards)
    }

    val guardsOk =
      if(guards.nonEmpty ) {
        checkGuards(0, guards)._1
      }
      else
        true
    if(!guardsOk){
      failureValue
    }
    else {
      f
    }
  }


  def executeResult[T](source:String,
                       action:String,
                       tag:String = "",
                       audit:Boolean = false,
                       data:Option[Any],
                       call:() => T ): Result[T] =
  {
    val result = try
    {
      val resultValue = Option(call())
      (true, "", resultValue)
    }
    catch {
      case ex:Exception =>
      {
        (false, s"Error performing action $action on $source with tag $tag. $ex", None)
      }
    }
    val success = result._1
    val message = result._2
    val resData = result._3
    successOrError(success, resData, Option(message), Option(tag))
  }


  def defaultOrExecute[T](condition:Boolean, defaultValue:T, f: => T): T = {
    if(condition){
      defaultValue
    }
    else
      f
  }


  def getOrElse[K,V](map:Map[K,V], key:K, defaultVal:V): V = {
    Option(map).fold(defaultVal)( m => if(m.contains(key)) map(key) else defaultVal)
  }


  def getStringByOrder(key:String, f1:String=>Option[String], f2:String => Option[String]): String = {
    val result1 = f1(key)

    // f1 => none ? then try f2
    val finalResult = result1.fold[String]( f2(key).getOrElse("") ) ( res1Value => {

      // check f1 value for empty string value
      val f1Orf2Value = if (Strings.isNullOrEmpty(res1Value)) f2(key).getOrElse("") else res1Value

      f1Orf2Value
    })
    finalResult
  }


  def getStringByOrderOrElse(key:String,
                             f1:String => Option[String],
                             f2:String => Option[String],
                             defaultValue:String ): String = {
    val result1 = f1(key)

    // f1 => none ? then try f2
    val finalResult = result1.fold[String]( f2(key).getOrElse(defaultValue) ) ( res1Value => {

      // check f1 value for empty string value
      val f1Orf2Value =
        if (Strings.isNullOrEmpty(res1Value)) {
        f2(key).getOrElse(defaultValue)
      }
      else {
        res1Value
      }
      f1Orf2Value
    })
    finalResult
  }


  /**
    * Not a true flatten in the recursive sense.
    * @param item
    * @return
    */
  def flatten(item:Any): Option[Any] = {
    item match {
      case null             => None
      case Unit             => None
      case None             => None
      case s:Option[Any]    => s
      case r:Result[Any]    => r.toOption
      case _                => Option(item)
    }
  }
}
