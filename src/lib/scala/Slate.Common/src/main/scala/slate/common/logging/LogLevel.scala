package slate.common.logging

/**
 * Created by kv on 10/14/2015.
 */
object LogLevel extends Enumeration {
  type LogLevel = Value
  val Debug, Info, Warn, Error, Fatal = Value
}
