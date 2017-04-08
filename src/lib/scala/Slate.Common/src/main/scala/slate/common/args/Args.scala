/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.args



import slate.common.{InputFuncs, DateTime, Inputs, Result}
import slate.common.Funcs._


/**
  * Container for parsed command line arguments that are either named or positional.
 *  Also holds an optional action representing a call to some method or URI in the format
 *  {area.name.action} which is expected to come before the arguments.
 *
 *  @example usage: app.users.invite -email="john@gmail.com" -role="guest"
  *
  * @param line        : the raw line ( if available )
  * @param raw         : the raw text that was parsed into arguments.
  * @param action      : "app.users.invite"
  * @param actionVerbs : ["app", "users", "invite" ]
  * @param prefix      : the letter used to prefix each key / name of named parameters e.g. "-"
  * @param separator   : the letter used to separate the key / name with value e.g. ":"
  * @param _namedArgs  : the map of named arguments ( key / value ) pairs
  * @param _indexArgs  : the list of positional arguments ( index based )
  */
class Args(
           val line        :String      ,
           val raw         :List[String],
           val action      :String      ,
           val actionVerbs :List[String],
           val prefix      :String = "-",
           val separator   :String = "=",
           private val _namedArgs:Option[Map[String,String]] = None,
           private val _indexArgs:Option[List[String]]       = None,
           private val _decryptor:Option[(String) => String] = None) extends Inputs
{

  private val _metaIndex = 0


  /**
   * gets read-only map of key-value based arguments
 *
   * @return
   */
  val named:Map[String,String] = _namedArgs.getOrElse(Map[String,String]())


  /**
   * gets read-only list of index/positional based arguments.
 *
   * @return
   */
  val positional:List[String] = _indexArgs.getOrElse(List[String]())


  /**
    * gets the size of all the arguments ( named + positional )
    *
    * @return
    */
  def size():Int = {
    _namedArgs.fold(0)( named      => named.size ) +
    _indexArgs.fold(0)( positional => positional.size )
  }


  /**
   * True if there are 0 arguments.
    *
    * @return
   */
  def isEmpty: Boolean = named.isEmpty && positional.isEmpty


  /**
   * returns true if there is only 1 argument with value:  --version -version /version
   * which shows the version of the task
    *
    * @return
   */
  def isVersion: Boolean = ArgsFuncs.isVersion(positional, _metaIndex)


  /**
   * Returns true if there is only 1 positional argument with value: pause -pause /pause
   * This is useful when running a program and then giving time to attach a debugger
    *
    * @return
   */
  def isPause: Boolean = ArgsFuncs.isPause(positional, _metaIndex)


  /**
   * Returns true if there is only 1 positional argument with value: exit -exit /exit
   * This is useful when running a program and then giving time to attach a debugger
    *
    * @return
   */
  def isExit: Boolean = ArgsFuncs.isExit(positional, _metaIndex)


  /**
   * returns true if there is only 1 argument with value: --help -help /? -? ?
    *
    * @return
   */
  def isHelp: Boolean = ArgsFuncs.isHelp(positional, _metaIndex)


  /**
   * returns true if there is only 1 argument with value -about or -info
 *
   * @return
   */
  def isInfo: Boolean = ArgsFuncs.isMetaArg(positional, _metaIndex, "about", "info")


  /**
   * gets the verb at the supplied position
   * @param pos
   * @return
   */
  def getVerb(pos:Int): String = getListValueOrElse(actionVerbs, pos, "")


  /**
   * get the value of the indexed argumentd ( after named arguments ) at the supplied pos
   * @param pos
   * @return
   */
  def getValueAt(pos:Int): String = getListValueOrElse(_indexArgs, pos, "")


  /**
   * Methods to get basic values
   * NOTE: There are other methods for getStringOpt, and getStringOrElse
   * @param key
   * @return
   */
  override def getString   (key: String) : String   = InputFuncs.decrypt(named(key), _decryptor)
  override def getDate     (key: String) : DateTime = InputFuncs.convertDate(named(key))
  override def getBool     (key: String) : Boolean  = named(key).toBoolean
  override def getInt      (key: String) : Int      = named(key).toInt
  override def getLong     (key: String) : Long     = named(key).toLong
  override def getDouble   (key: String) : Double   = named(key).toDouble
  override def getFloat    (key: String) : Float    = named(key).toFloat

  override def get(key: String) : Option[Any]             = if (named.contains(key)) Option(named(key)) else None
  override def getObject(key: String): Option[AnyRef]     = if (named.contains(key)) Option(named(key).asInstanceOf[AnyRef]) else None
  override def containsKey(key: String): Boolean =
  {
    _namedArgs.fold(false)( args => args.contains(key))
  }
}


object Args
{
  def apply():Args = {
    new Args("", List[String](), "", List[String]())
  }


  /**
    * Parses the arguments using the supplied prefix and separator for the args.
    * e.g. users.activate -email:kishore@gmail.com -code:1234
    *
    * @param line     : the raw line of text to parse into {action} {key/value}* {position}*
    * @param target   : the target object to apply the command line arguments on
    * @param prefix   : the prefix for a named key/value pair e.g. "-" as in -env:dev
    * @param sep      : the separator for a nmaed key/value pair e.g. ":" as in -env:dev
    * @param hasAction: whether the line of text has an action before any named args.
    *                   e.g. name.action {namedarg}*
    * @return
    */
  def apply(line:String, target:Any, prefix:String = "-", sep:String = ":",
            hasAction:Boolean = false): Result[Args] =
  {
    val result = new ArgsService().parse(line, prefix, sep, hasAction)
    result
  }


  /**
   * Parses the arguments using the supplied prefix and separator for the args.
   * e.g. users.activate -email:kishore@gmail.com -code:1234
    *
    * @param line     : the raw line of text to parse into {action} {key/value}* {position}*
   * @param prefix   : the prefix for a named key/value pair e.g. "-" as in -env:dev
   * @param sep      : the separator for a nmaed key/value pair e.g. ":" as in -env:dev
   * @param hasAction: whether the line of text has an action before any named args.
   *                   e.g. name.action {namedarg}*
   * @return
   */
  def parse(line:String, prefix:String = "-", sep:String = ":", hasAction:Boolean = false)
    : Result[Args] =
  {
    new ArgsService().parse(line, prefix, sep, hasAction)
  }


  /**
    * Parses the arguments using the supplied prefix and separator for the args.
    * e.g. users.activate -email:kishore@gmail.com -code:1234
    *
    * @param args     : the raw line of text to parse into {action} {key/value}* {position}*
    * @param prefix   : the prefix for a named key/value pair e.g. "-" as in -env:dev
    * @param sep      : the separator for a nmaed key/value pair e.g. ":" as in -env:dev
    * @param hasAction: whether the line of text has an action before any named args.
    *                   e.g. name.action {namedarg}*
    * @return
    */
  def parseArgs(args:Array[String], prefix:String = "-", sep:String = ":", hasAction:Boolean = false)
  : Result[Args] =
  {
    // build a single line from args
    val line = if( Option(args).fold(false)( a => a.nonEmpty)) {
      args.indices.foldLeft("")( (text, ndx) => {
        if ( ndx > 0 ){
          text + " " + args(ndx)
        }
        else {
          args(ndx)
        }
      })
    }
    else
      ""
    new ArgsService().parse(line, prefix, sep, hasAction)
  }
}
