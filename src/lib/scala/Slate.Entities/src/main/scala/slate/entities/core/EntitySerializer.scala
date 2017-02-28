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

package slate.entities.core

import slate.common.serialization.{SerializerJson, SerializerBase, SerializerProps}

class EntitySerializer {

  def toStringProps(item:Entity, mapper:EntityMapper):String =
  {
    toString( new SerializerProps(), item, mapper)
  }


  def toStringJson(item:Entity, mapper:EntityMapper):String =
  {
    toString(new SerializerJson(), item, mapper)
  }


  def toString(serializer:SerializerBase, item:Entity, mapper:EntityMapper):String =
  {
    val content = serializer.serialize(item)
    content
  }
}
