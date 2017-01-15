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
package slate.common.serialization

import slate.common.{Indenter, Strings}

class ObjectBuilder(val indentEnabled:Boolean, protected val _indent:String) {

  protected val _indenter = new Indenter()
  protected val _buffer = new StringBuilder()


  def begin():Unit = {
  }


  def indenter:Indenter = _indenter


  def putString(key:String, value:String) :Unit = {
  }


  def putLine(text:String) :Unit = {
    _buffer.append(_indent + text + Strings.newline())
  }


  def newLine():Unit = _buffer.append(Strings.newline())


  def end():Unit = {  }


  override def toString():String = _buffer.toString()
}
