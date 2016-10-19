package slate

import slate.common.logging.LogLevel
import slate.common.logging.LogLevel.LogLevel
import slate.common.logging.LogLevel.LogLevel

/**
 * Convenience methods on the package to be used for
 * components internal to slate.common project
 */
package object common {


  implicit class IntExtensions(val i: Int)
  {
    def daysAgo() = DateTime.today().addDays(-1*i)

    def daysFromNow() = DateTime.today().addDays(i)
  }
}
