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

package slate.core.apis

import slate.common._
import slate.common.results.ResultTimed
import slate.core.apis.support.{ApiCallReflect}
import slate.core.common.AppContext

/**
  * Base class for any Api, provides lookup functionality to check for exposed api actions.
  */
class ApiBase {

  protected  val _lookup = new ListMap[String, ApiCallReflect]()

  /**
   * The context of the application. Contains references to the
   * 1. selected environment
   * 2. logger
   * 3. config
   * 4. encryptor
   * 5. authenticator
   * 6. app info and more
   */
  var context:AppContext = null


  /**
   * The api parent container that actually executes the calls on this api
   * and has a register of all the apis.
   */
  var container:ApiContainer = null


  /**
   * hook to allow api to initialize itself
   */
  def init():Unit =
  {
  }


  /**
   * gets a list of all the actions / methods this api supports.
 *
   * @return
   */
  def actions():ListMap[String,ApiCallReflect] =
  {
    _lookup.clone()
  }


  /**
   * whether or not the action exists in this api
    *
    * @param action : e.g. "invite" as in "users.invite"
   * @return
   */
  def contains(action:String):Boolean =
  {
    _lookup.contains(action)
  }


  /**
   * gets the value with the supplied key(action)
    *
    * @param action
   * @return
   */
  def apply(action:String):ApiCallReflect =
  {
    if(!contains(action))
      throw new IllegalArgumentException("action : " + action + " not found")
    _lookup(action)
  }


  /**
   * adds a key/value to this collection
    *
    * @param action
   * @param value
   */
  def update(action:String, value:ApiCallReflect) =
  {
    _lookup(action) = value
  }


  /**
   * benchmarks a function call by tracking the start, end and duration of the call.
    *
    * @param callback : the call to benchmark
   * @return : OperationResultBenchmarked
   */
  def bench(callback:()=>Any ):ResultTimed[Any] =
  {
    var result:Any = null

    val resultTimed = Timer.once("", () => {
      result = callback()
    })

    resultTimed.withData(result)
  }
}
