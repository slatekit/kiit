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


import slate.common.results.ResultCode
import slate.common.{Inputs, Result}


/**
  * Container for parsed command line arguments that are either named or positional.
 *  Also holds an optional action representing a call to some method or URI in the format
 *  {area.name.action} which is expected to come before the arguments.
 *
 *  @example usage: app.users.invite -email="john@gmail.com" -role="guest"
  *
  * @param action      : "app.users.invite"
  * @param actionVerbs : ["app", "users", "invite" ]
  * @param prefix      : the letter used to prefix each key / name of named parameters e.g. "-"
  * @param separator   : the letter used to separate the key / name with value e.g. ":"
  * @param raw         : the raw text that was parsed into arguments.
  * @param _namedArgs  : the map of named arguments ( key / value ) pairs
  * @param _indexArgs  : the list of positional arguments ( index based )
  */
class Args(val action:String,
           val actionVerbs:List[String],
           val prefix:String,
           val separator:String,
           val raw:List[String],
           private val _namedArgs:Map[String,String],
           private val _indexArgs:List[String] ) extends Inputs
{

  private val _metaIndex = 0


  def named:Map[String,String] = _namedArgs


  def positional:List[String] = _indexArgs


  /**
    * gets the size of all the arguments ( named + positional )
    *
    * @return
    */
  def size():Int =
  {
    var count = 0
    if (_namedArgs != null ) count = _namedArgs.size
    if ( _indexArgs!= null ) count = count + _indexArgs.size

    count
  }


  /**
   * True if there are 0 arguments.
    *
    * @return
   */
  def isEmpty: Boolean =
  {
    ( named == null || named.size == 0) && (positional == null || positional.size == 0)
  }


  /**
   * returns true if there is only 1 argument with value:  --version -version /version
   * which shows the version of the task
    *
    * @return
   */
  def isVersion: Boolean =
  {
    ArgsHelper.isVersion(positional, _metaIndex)
  }


  /**
   * Returns true if there is only 1 positional argument with value: pause -pause /pause
   * This is useful when running a program and then giving time to attach a debugger
    *
    * @return
   */
  def isPause: Boolean =
  {
    ArgsHelper.isPause(positional, _metaIndex)
  }


  /**
   * Returns true if there is only 1 positional argument with value: exit -exit /exit
   * This is useful when running a program and then giving time to attach a debugger
    *
    * @return
   */
  def isExit: Boolean =
  {
    ArgsHelper.isExit(positional, _metaIndex)
  }


  /**
   * returns true if there is only 1 argument with value: --help -help /? -? ?
    *
    * @return
   */
  def isHelp: Boolean =
  {
    ArgsHelper.isHelp(positional, _metaIndex)
  }


  /// <summary>
  /// Returns true if there is only 1 argument with value: --About -About /About
  /// </summary>
  def isInfo: Boolean =
  {
    ArgsHelper.isMetaArg(positional, _metaIndex, "about", "info")
  }


  def getVerb(pos:Int): String =
  {
    if(actionVerbs == null || pos >= actionVerbs.size )
      return ""

    actionVerbs(pos)
  }


  def getValueAt(pos:Int): String =
  {
    if ( _indexArgs == null || _indexArgs.size == 0 )
      return ""
    if (pos >= _indexArgs.size)
      return ""

    _indexArgs(pos)
  }


  /// <summary>
  override def getValue(key: String): AnyVal =
  {
    if ( !containsKey(key) )
      throw new IllegalArgumentException("key not found in arguments : " + key)

    _namedArgs(key).asInstanceOf[AnyVal]
  }


  /// <summary>
  override def getObject(key: String): AnyRef =
  {
    if ( !containsKey(key) ) return null

    _namedArgs(key).asInstanceOf[AnyRef]
  }


  /// <summary>
  override def containsKey(key: String): Boolean =
  {
    if(_namedArgs == null){
      return false
    }
    _namedArgs.contains(key)
  }
}


object Args
{
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
    var line = ""
    if(args != null && args.size > 0){
      for(i <- 0 until args.size ){
        if ( i > 0 ){
          line = line + " " + args(i)
        }
        else {
          line = args(i)
        }
      }
    }
    new ArgsService().parse(line, prefix, sep, hasAction)
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
}
