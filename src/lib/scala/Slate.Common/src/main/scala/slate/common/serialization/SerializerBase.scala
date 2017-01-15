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

import slate.common.{Indenter, Strings, Reflector}

class SerializerBase {
  protected val _buffer = new StringBuilder()
  protected val _identer = new Indenter()


  override def toString():String =
  {
    _buffer.toString()
  }


  def serialize(item:AnyRef):String =
  {
    // Get fields
    val fields = Reflector.getFieldsDeclared(item)
    val maxFieldLength = onBeforeSerialize(fields)

    // Begin
    onVisitItemBegin(item, 1, 1)

    val len = fields.size

    fields.indices.foreach( ndx => {
      val field = fields(ndx)
      // Get name/value
      val propName = field.symbol.name.toString.trim()
      val value = Some(Reflector.getFieldValue(item, propName))

      // Visit each field
      onVisitFieldBegin(maxFieldLength, item, propName, value, ndx, len)
      onVisitFieldEnd(maxFieldLength, item, propName, value, ndx, len)
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


  def onVisitFieldBegin(maxFieldLength:Int, item:Any, name:String, value:Option[Any], pos:Int, total:Int):Unit =
  {

  }


  def onVisitFieldEnd(maxFieldLength:Int, item:Any, name:String, value:Option[Any], pos:Int, total:Int):Unit =
  {

  }


  protected def onBeforeSerialize(fields:List[FieldMirror]):Int = {

    val max = fields.maxBy[Int]( f => f.symbol.name.toString.length)
    max.symbol.name.toString.length
  }


  protected def append(text:String)
  {
    _buffer.append(_identer.value() + text)
  }
}
