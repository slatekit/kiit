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

package slate.common.args


import slate.common.{Looper}
import scala.collection.mutable.{ListBuffer, Map}


object ArgsHelper {


  /**
   * returns true if there is only 1 argument with value: help ?
   * @return
   */
  def isHelp = isMetaArg(_:List[String], _:Int, "help", "?")


  /**
   * returns true if there is only 1 argument with value: version | ver
   * @return
   */
  def isVersion = isMetaArg(_:List[String], _:Int, "version", "ver")


  /**
    * returns true if there is only 1 argument with value: version | ver
    * @return
    */
  def isAbout = isMetaArg(_:List[String], _:Int, "about", "info")


  /**
   * returns true if there is only 1 argument with value: pause
   * @return
   */
  def isPause = isMetaArg(_:List[String], _:Int, "pause", "ver")


  /**
   * returns true if there is only 1 argument with value: --exit -quit /? -? ?
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
    if(positional == null || positional.size == 0)
      return false
    if(pos >= positional.size)
      return false

    val arg = positional(pos)
    var isMatch = false
    var ndx = 0
    while(ndx < possibleMatches.size && !isMatch)
    {
      val possibleMatch = possibleMatches(ndx)
      if(possibleMatch == arg)
      {
        isMatch = true
      }
      else if( "-" + possibleMatch == arg)
      {
        isMatch = true
      }
      else if( "--" + possibleMatch == arg)
      {
        isMatch = true
      }
      else if("/" + possibleMatch == arg)
      {
        isMatch = true
      }
      ndx = ndx + 1
    }
    isMatch
  }


  /**
   * parses the action from the command line args
   * e.g. app.users.activate -id=2
   * the action would be "app.users.activate"
   *
   * @param args
   * @param prefix
   * @return
   */
  def parseAction(args:List[String], prefix:String): (String, ListBuffer[String], Int, Int) =
  {
    var action = ""
    var lastIndex = 0
    val verbs = ListBuffer[String]()
    var verbCount = 0
    var lastIndexIncluded = 0

    Looper.loop(args, 0, (ndx:Int) =>
    {
      val text = args(ndx)
      var returnVal = true

      // CASE 1: An action can not be more than 3 args
      // e.g. area.name.action or stop once you hit a named arg -env:dev
      if(lastIndex >= 5 || verbCount >= 3 || text == prefix)
      {
        returnVal = false
      }
      // CASE 2: "." name.action
      // e.g. area.name.action
      else if(text == ".")
      {
        action += text
      }
      // CASE 3: action without "."
      // e.g. name action
      else {
        val isLastCharDot = action.endsWith(".")
        if (isLastCharDot) {
          action += text
        }
        else if (verbCount == 0) {
          action += text
        }
        else {
          action += "." + text
        }

        verbCount = verbCount + 1
        verbs.append(text)
      }
      // Ensure the lastIndex includes index of the word included
      if(returnVal)
      {
        lastIndex = ndx
      }
      returnVal
    })

    (action, verbs, verbCount, lastIndex)
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
    var lastIndex = startIndex
    var parseState = "none"
    var lastKey = ""
    var lastVal = ""
    val resultArgs = Map[String,String]()

    Looper.loop(args, startIndex, (ndx:Int) =>
    {
      val curr = args(ndx)
      val next = if(args.size <= (ndx+1)) "" else args(ndx+1)
      var returnVal = true

      // "-" beginning of key/value pair -env:dev
      if(curr == prefix)
      {
        parseState = "key"
        lastVal = ""
      }
      // ":" separator between key/value pairs -eng:dev
      else if(curr == sep)
      {
        parseState = "value"
        lastVal = ""
      }
      // KEY ( 2 words )
      else if(parseState == "key" && ( curr == "." ) )
      {
        lastKey += curr
        lastVal = "."
      }
      // KEY ( 2 words )
      else if(parseState == "key" && ( lastVal == "." ) )
      {
        lastKey += curr
        lastVal = ""
      }
      // KEY ( 1 word )
      else if(parseState == "key")
      {
        lastKey = curr
        lastVal = ""
      }
      else if(parseState == "value" && curr == "." )
      {
        lastVal = lastVal + curr
      }
      // VAL
      else if(parseState == "value")
      {
        if(lastVal != "") {
          lastVal = lastVal + curr
        }
        else
        {
          lastVal = curr
        }
        if(curr == "\"\"")
        {
          lastVal = ""
        }
        if(next == null || next != ".") {
          resultArgs(lastKey) = lastVal
          lastVal = ""
        }
      }
      // Must be positional parameter
      else
      {
        returnVal = false
      }
      // Ensure the lastIndex includes index of the word included
      if(returnVal)
      {
        lastIndex = ndx
      }
      returnVal
    })

    (resultArgs, lastIndex)
  }
}
