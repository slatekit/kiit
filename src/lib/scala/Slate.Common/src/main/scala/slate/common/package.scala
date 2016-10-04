package slate

import slate.common.logging.LogLevel
import slate.common.logging.LogLevel.LogLevel
import slate.common.logging.LogLevel.LogLevel

/**
 * Convenience methods on the package to be used for
 * components internal to slate.common project
 */
package object common {

  //def logger: Function4[LogLevel, String, String, Exception, Unit] =  null
  def logger: (LogLevel, String, String, Exception) => Unit = null


  // ========================================================
  // There are all methods related to the To do
  // ========================================================
  def not_implemented( tag: String = "", msg: String = "") =
  {
    Todo.notImplemented(tag, msg)
  }


  def implement( tag: String = "", msg: String = "") =
  {
    Todo.implement(tag, msg)
  }


  def refactor( tag: String = "", msg: String = "") =
  {
    Todo.implement(tag, msg)
  }


  def bug( tag: String = "", msg: String = "") =
  {
    Todo.implement(tag, msg)
  }


  implicit class IntExtensions(val i: Int)
  {
    def daysAgo() = DateTime.today().addDays(-1*i)

    def daysFromNow() = DateTime.today().addDays(i)
  }
}
