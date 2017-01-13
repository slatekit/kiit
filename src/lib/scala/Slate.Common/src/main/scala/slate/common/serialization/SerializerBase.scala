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

package slate.common.serialization

import scala.reflect.runtime.universe.FieldMirror

import slate.common.{Strings, Reflector}

class SerializerBase {
  protected var _indentLevel = 0
  protected var _buffer = ""
  protected var _indent = ""
  protected var _maxFieldLength = 10


  override def toString():String =
  {
    _buffer
  }


  def serialize(item:AnyRef):String =
  {
    // Get fields
    val fields = Reflector.getFieldsDeclared(item)
    onBeforeSerialize(fields)

    // Begin
    onVisitItemBegin(item, 1, 1)

    val len = fields.size

    fields.indices.foreach( ndx => {
      val field = fields(ndx)
      // Get name/value
      val propName = field.symbol.name.toString.trim()
      val value = Some(Reflector.getFieldValue(item, propName))

      // Visit each field
      onVisitFieldBegin(item, propName, value, ndx, len)
      onVisitFieldEnd(item, propName, value, ndx, len)
    })

    // End
    onVisitItemEnd(item, 1, 1)

    toString()
  }


  def onVisitItemBegin(item:Any, pos:Int, total:Int):Unit =
  {

  }


  def onVisitItemEnd(item:Any, pos:Int, total:Int):Unit =
  {

  }


  def onVisitFieldBegin(item:Any, name:String, value:Option[Any], pos:Int, total:Int):Unit =
  {

  }


  def onVisitFieldEnd(item:Any, name:String, value:Option[Any], pos:Int, total:Int):Unit =
  {

  }


  protected def onBeforeSerialize(fields:List[FieldMirror]):Unit = {

    val max = fields.maxBy[Int]( f => f.symbol.name.toString.length)
    _maxFieldLength = max.symbol.name.toString.length
  }


  protected def append(text:String)
  {
    _buffer += _indent + text
  }


  protected def indentInc():Unit =
  {
    _indentLevel = _indentLevel + 1
    setIndent()
  }


  protected def indentDec():Unit =
  {
    _indentLevel = _indentLevel - 1
    setIndent()
  }


  protected def setIndent()
  {
    if(_indentLevel == 0) {
      _indent = ""
    }
    else {
      0.until(_indentLevel).foreach( i => _indent += "\t")
    }
  }
}
