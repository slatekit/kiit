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

object StringHelpers {

  implicit class StringExt(text:String) {

    /**
     * pad the text to be x number of chars max
     * @param max
     * @return
     */
    def pad(max:Int):String =
    {
      if (text.length == max) {
        text
      }
      else {
        var pad = ""
        var count = 0
        while (count < max - text.length) {
          pad += " "
          count = count + 1
        }
        text + pad
      }
    }


    /**
     * pad the text to be x number of chars max
     * @param max
     * @return
     */
    def repeat(max:Int):String =
    {
      if (max == 0) ""
      else if (max == 1) text
      else {
        var finalText = ""
        var count = 0
        while (count < max - text.length) {
          finalText += text
          count = count + 1
        }
        finalText
      }
    }


    /**
     * converts the text to valid url path which means:
     * 1. trim leading / trailing spaces
     * 2. remove spaces and replace with '-'
     * @return
     */
    def toUrlPath():String =
    {
      text match {
        case null => ""
        case "" => ""
        case _ => text.trim().replaceAllLiterally(" ", "-")
      }
    }
  }
}
