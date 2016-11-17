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

package slate.core.datasource

/**
 * Created by kv on 10/19/2015.
 */
trait DataSourceCallbacks {
  def onDataSourceAvailable(sender: AnyRef, args: DataSourceEventArgs)
  def onDataSourceError(sender: AnyRef, args: DataSourceEventArgs)
}
