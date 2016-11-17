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

package slate.core.cache

import slate.common.Todo

/**
 * Created by kv on 10/23/2015.
 */
class Cache extends ICache {


  Todo.implement("core.cache", "Not implemented for now")

  override def get(key: String): Any = ???

  override def clear(key: String): Unit = ???

  override def put(key: String, obj: Any, callback: Option[() => AnyRef]): Unit = ???

  override def remove(key: String): Unit = ???

  override def contains(key: String): Boolean = ???
}
