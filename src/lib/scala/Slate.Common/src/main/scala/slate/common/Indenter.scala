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

package slate.common

class Indenter {
  protected var _indentLevel = 0
  protected var _buffer = ""
  protected var _indent = ""
  protected var _maxFieldLength = 10


  def value():String = _indent


  def inc():Unit =
  {
    _indentLevel = _indentLevel + 1
    calc()
  }


  def dec():Unit =
  {
    _indentLevel = _indentLevel - 1
    calc()
  }


  private def calc()
  {
    if(_indentLevel == 0) {
      _indent = ""
    }
    else {
      0.until(_indentLevel).foreach( i => _indent += "\t")
    }
  }
}
