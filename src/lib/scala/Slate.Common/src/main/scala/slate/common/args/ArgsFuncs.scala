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


import slate.common.{Strings}
import slate.common.Loops._


object ArgsFuncs {


  /**
   * returns true if there is only 1 argument with value: help ?
    *
    * @return
   */
  def isHelp = isMetaArg(_:List[String], _:Int, "help", "?")


  /**
   * returns true if there is only 1 argument with value: version | ver
    *
    * @return
   */
  def isVersion = isMetaArg(_:List[String], _:Int, "version", "ver")


  /**
    * returns true if there is only 1 argument with value: version | ver
    *
    * @return
    */
  def isAbout = isMetaArg(_:List[String], _:Int, "about", "info")


  /**
   * returns true if there is only 1 argument with value: pause
    *
    * @return
   */
  def isPause = isMetaArg(_:List[String], _:Int, "pause", "ver")


  /**
   * returns true if there is only 1 argument with value: --exit -quit /? -? ?
    *
    * @return
   */
  def isExit = isMetaArg(_:List[String], _:Int, "exit", "quit")


  /**
   * checks for meta args ( e.g. request for help, version etc )
   * e..g
   * -help    |  --help     |  /help
   * -about   |  --about    |  /about
   * -version |  --version  |  /version
   *
   * @param positional
   * @param pos
   * @param possibleMatches
   * @return
   */
  def isMetaArg(positional:List[String], pos:Int, possibleMatches:String*): Boolean =
  {
    val any = Option(positional).fold(false)( p => p.nonEmpty)
    val posOk = Option(positional).fold(false)( p => pos < p.size)

    if (!any || !posOk){
      false
    }
    else {
      val arg = positional(pos)
      possibleMatches.foldLeft(false)((isMatch, text) => {
        if (text == arg) {
          true
        }
        else if ("-" + text == arg) {
          true
        }
        else if ("--" + text == arg) {
          true
        }
        else if ("/" + text == arg) {
          true
        }
        else
          isMatch
      })
    }
  }


  /**
   * parses the action from the command line args
   * e.g. ["app", ".", "users", ".", "activate", "-", "id", "=", "2" ]
   * the action would be "app.users.activate"
   *
   * @param args
   * @param prefix
   * @return ( action, actions, actionCount, end index )
   *         ( "app.users.activate", ["app", 'users", "activate" ], 3, 5 )
   */
  def parseAction(args:List[String], prefix:String): (String, List[String], Int, Int) =
  {
    // Get the first index of arg prefix ( e.g. "-" or "/"
    val indexPrefix = args.indexWhere( arg => arg == prefix )

    // Get index after action "app.users.activate"
    val indexLast = if(indexPrefix < 0 ) args.size else indexPrefix

    // Get all the words until last index
    val actions = args.slice(0, indexLast)
                      .filter( text => !Strings.isNullOrEmpty(text)
                              && (text.trim() == "?" || text.matches("^[a-zA-Z0-9]*$") ) )

    val action = actions.mkString(".")
    (action, actions, actions.size, indexLast)
  }


  /**
   * parses all the named args using prefix and separator e.g. -env=dev
   *
   * @param args
   * @param startIndex
   * @param prefix
   * @param sep
   * @return
   */
  def parseNamedArgs(args:List[String], startIndex:Int, prefix:String, sep:String)
    : (Map[String,String], Int) =
  {
    val resultArgs = scala.collection.mutable.Map[String,String]()

    // Parses all named args e..g -a=1 -b=2
    // Keep looping until the index where the named args ends
    // NOTE: Get the index after the last named arg.
    val endIndex = repeatWithIndex(startIndex, args.size, (ndx) => {

      // GIVEN: -a=1 the tokens are : ( "-", "a", "=", "1" )
      val text = args(ndx)

      // e.g. "-a=1"
      val nextIndex =

        // Prefix ? "-"
        if(text == prefix ){

          // Get "a" "1" from ( "a", "=", "1" )
          val keyValuePair = parseKeyValuePair(ndx, sep,args)

          // Matched so put into map
          val advance = keyValuePair.fold[Int](args.size)( kv => {
            resultArgs(kv._1) = kv._2
            kv._3
          })

          // The index position after the named arg
          advance
        }
        else
          ndx + 1
      nextIndex
    })

    (resultArgs.toMap, endIndex)
  }


  def parseKeyValuePair(ndx:Int, sep:String, args:List[String]):Option[(String, String, Int)] = {

    // example: -a=1
    // prefix: "-", key: "a", sep: "=", value="1"
    val pos = ndx
    if ( pos + 3 < args.size ) {

      // Move past "-"
      val posKey = pos + 1

      // Build the key e.g. "log" or "log.level"
      val keyBuff = new StringBuilder(args(posKey))

      // Move to next part of key
      val posKeyExt = posKey + 1

      // Keep building key until "." is done
      val posEndKey = repeatWithIndexAndBool(posKeyExt, args.size, (posNext) => {
        if(args(posNext) == "."){
          keyBuff.append("." + args(posNext + 1))
          (true, posNext + 2)
        }
        else {
          (false, posNext)
        }
      })

      // Move past "="
      val posVal = posEndKey + 1

      // Get value
      val value = args(posVal)

      // Now get key/value
      val key = keyBuff.toString()

      // Move past value
      val end = posVal + 1

      Some((key, value, end))
    }
    else
      None
  }
}
