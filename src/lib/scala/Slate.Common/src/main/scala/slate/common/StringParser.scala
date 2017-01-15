/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common

import scala.collection.mutable.Map
/**
  * Created by kreddy on 3/22/2016.
  */
class StringParser(private val _content:String ) {

  private val _extracts = Map[String,String]()
  private var _pos = 0
  private var _lastMatch = false


  def extracts:Map[String,String] =
  {
    _extracts
  }


  def saveUntil(token: String, name:String, ensure:Boolean = true): StringParser =
  {
    val start = _pos
    moveInternal(token, ensure)
    if(_lastMatch)
    {
      val end = _pos - token.length
      val content = extract(start, end)
      _extracts(name) = content
    }
    this
  }


  def moveTo(token: String, ensure:Boolean = true): StringParser =
  {
    moveInternal(token, ensure)
    this
  }


  def moveInternal(token: String, ensure:Boolean = true): StringParser =
  {
    val ndxMatch = _content.indexOf(token, _pos)
    if(ensure && ndxMatch < 0 ){

    }
    else {
      _lastMatch = ndxMatch >= 0

      if (ndxMatch >= 0) {
        // Update pos to next position
        _pos = ndxMatch + token.length
      }
    }
    this
  }


  def extract(start:Int, end:Int):String =
  {
    _content.substring(start, end)
  }

}
