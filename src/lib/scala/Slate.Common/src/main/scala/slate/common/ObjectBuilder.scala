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

class ObjectBuilder(val indentEnabled:Boolean, protected var _indent:String) {

  protected var _indentLevel = 0
  protected var _buffer = ""


  def begin():Unit = {
  }


  def putString(key:String, value:String) :Unit = {
    ""
  }


  def putLine(text:String) :Unit = {
    _buffer = _buffer + _indent + text + Strings.newline()
  }


  def newLine():Unit = {
    _buffer = _buffer + Strings.newline()
  }


  def end():Unit = {
  }


  def indentInc():Unit =
  {
    _indentLevel = _indentLevel + 1
    setIndent()
  }


  def indentDec():Unit =
  {
    _indentLevel = _indentLevel - 1
    setIndent()
  }


  def setIndent()
  {
    if(_indentLevel == 0)
    {
      _indent = ""
      return
    }
    for(ndx <- 0 until _indentLevel)
    {
      _indent += "\t"
    }
  }


  override def toString():String = {
    _buffer
  }
}
