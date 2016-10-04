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

package slate.entities.core

import slate.common.Reflector
import slate.common.serialization.{SerializerJson, SerializerBase, SerializerProps}

class EntitySerializer {

  def serializeToProps(item:IEntity, mapper:EntityMapper):String =
  {
    val serializer = new SerializerProps()
    serialize(serializer, item, mapper)
  }

  def serializeToJson(item:IEntity, mapper:EntityMapper):String =
  {
    val serializer = new SerializerJson()
    serialize(serializer, item, mapper)
  }


  def serialize(serializer:SerializerBase, item:IEntity, mapper:EntityMapper):String =
  {
    val content = serializer.serialize(item)
    content
    /*
    // Begin
    serializer.onVisitItemBegin(item, 1, 1)

    val model = mapper.model()
    val len = model.fields.size
    for( ndx <- 0 until len) {
      val mapping = model.fields(ndx)
      val propName = mapping.name
      val value = Some(Reflector.getFieldValue(item, mapping.name))
      serializer.onVisitFieldBegin(item, propName, value, ndx, len)
      serializer.onVisitFieldEnd(item, propName, value, ndx, len)
    }

    serializer.onVisitItemEnd(item, 1, 1)

    serializer.toString()
    */
  }
}
