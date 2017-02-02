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

import java.util.concurrent.atomic.AtomicInteger

class Indenter {
  val count = new AtomicInteger()


  def value():String = "\t" * count.get


  def inc():Int = count.incrementAndGet()


  def dec():Int = count.decrementAndGet()
}
