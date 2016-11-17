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

trait ICache {
  def get(key:String) : Any
  def put(key:String, obj:Any, callback: Option[() => AnyRef])
  def contains(key:String): Boolean
  def remove(key:String)
  def clear(key:String)
}
