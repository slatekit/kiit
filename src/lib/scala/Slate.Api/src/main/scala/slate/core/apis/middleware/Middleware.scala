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

package slate.core.apis.middleware

import java.util.concurrent.atomic.AtomicBoolean

import slate.common.info.About
import slate.common.results.ResultFuncs

/**
 * Base trait for the 3 different types of middle ware ( hooks, filters, controls )
 * 1. hooks   : for pre/post execution of api actions         ( can not modify the execution    )
 * 2. filters : for allowing/denying execution of api action  ( can only allow/deny execution   )
 * 3. controls: for controlling the execution of an api action( can handle the execution itself )
 */
trait Middleware
{
  /**
   * Info about the middleware including:
   * id, name, desc, company, version, url, etc.
   */
  val about:About


  /**
   * common/basic return values for the filter.
   * Used as values here to avoid excessive object creation
   */
  protected val success = ResultFuncs.ok()
  protected val bad_req = ResultFuncs.badRequest()

  /**
   * internal flag to enable/disable this middleware
   */
  private val flag = new AtomicBoolean(true)


  /**
   * Enables this middleware
   * @return
   */
  def enable(): Boolean = toggle(true)


  /**
   * Disables this middleware
   * @return
   */
  def disable():Boolean = toggle(false)


  /**
   * toggle this middleware
   * @param newValue
   * @return
   */
  def toggle(newValue:Boolean):Boolean = {
    flag.set(newValue)
    flag.get()
  }
}
