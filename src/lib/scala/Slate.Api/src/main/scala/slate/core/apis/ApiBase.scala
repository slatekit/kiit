/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.apis

import java.io.File
import java.nio.file.Paths

import slate.common._
import slate.common.encrypt.Encryptor
import slate.common.i18n.I18nStrings
import slate.common.logging.LoggerBase
import slate.common.results.{ResultCode, ResultTimed}
import slate.core.apis.core.Action
import slate.core.common.AppContext

/**
 * Base class for any Api, provides lookup functionality to check for exposed api actions.
 * @param context   : The context of the application ( logger, config, encryptor, etc )
 */
class ApiBase(val context:AppContext ) {

  val isErrorEnabled = false
  protected  val _lookup = new ListMap[String, Action]()


  /**
   * hook to allow api to initialize itself
   */
  def init():Unit = {}


  /**
   * gets a list of all the actions / methods this api supports.
 *
   * @return
   */
  def actions():ListMap[String,Action] = _lookup.clone()


  /**
   * whether or not the action exists in this api
    *
    * @param action : e.g. "invite" as in "users.invite"
   * @return
   */
  def contains(action:String):Boolean = _lookup.contains(action)


  /**
   * gets the value with the supplied key(action)
    *
    * @param action
   * @return
   */
  def apply(action:String):Action =
  {
    if(!contains(action))
      throw new IllegalArgumentException("action : " + action + " not found")
    _lookup(action)
  }


  /**
   * gets the value with the supplied key(action)
   *
   * @param action
   * @return
   */
  def getOpt(action:String):Option[Action] =
  {
    if(!contains(action))
      None
    else
      Some(_lookup(action))
  }


  /**
   * adds a key/value to this collection
    *
    * @param action
   * @param value
   */
  def update(action:String, value:Action) =
  {
    _lookup(action) = value
  }


  def onException(context:AppContext, request: Request, ex:Exception): Result[Any] = {
    FailureResult[Boolean](ResultCode.UNEXPECTED_ERROR, msg = Some("unexpected error in api"), err = Some(ex))
  }


  /**
    * gets a service from the context
    *
    * @param key
    * @tparam T
    * @return
    */
  def getSvc[T](key:String): Option[T] = context.svcs.fold[Option[T]](None)( s => s.get(key) )


  /**
   * benchmarks a function call by tracking the start, end and duration of the call.
    *
    * @param callback : the call to benchmark
   * @return : OperationResultBenchmarked
   */
  def bench[T](callback:() => T ):ResultTimed[T] = Timer.once[T]("", { callback()})


  protected def interpretUri(path:String): Option[String] = {
    val pathParts = Strings.substring(path, "://")
    pathParts.fold(Option(path))( parts => {
      val uri = parts._1
      val loc = parts._2
      uri match {
        case "user://"    => Option(new File(System.getProperty("user.home")     , loc).toString)
        case "temp://"    => Option(new File(System.getProperty("java.io.tmpdir"), loc).toString)
        case "file://"    => Option(new File(loc).toString)
        case "inputs://"  => Option(new File(this.context.dirs.get.pathToInputs  , loc).toString)
        case "outputs://" => Option(new File(this.context.dirs.get.pathToOutputs , loc).toString)
        case "logs://"    => Option(new File(this.context.dirs.get.pathToLogs    , loc).toString)
        case "cache://"   => Option(new File(this.context.dirs.get.pathToCache   , loc).toString)
        case _            => Option(path)
      }
    })
  }


  protected def writeToFile(msg:Option[Any], fileNameLocal:String, count:Int,
                          contentFetcher:(Option[Any]) => String):Option[String] = {

    msg.fold(Some("No items available"))( item => {
      val finalFileName = if(count == 0) fileNameLocal else fileNameLocal + "_" + count
      val path = interpretUri(finalFileName)
      val content = contentFetcher(msg)
      Files.writeAllText(path.get.toString, content)
      Some("File content written to : " + path)
    })
  }
}
