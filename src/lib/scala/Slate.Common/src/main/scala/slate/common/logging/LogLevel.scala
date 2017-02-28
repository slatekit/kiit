package slate.common.logging


/**
 * Created by kv on 10/14/2015.
 */
abstract class LogLevel(val name:String, val code:Int ){


  def <  (lv:LogLevel): Boolean = compareTo(lv) == -1
  def <= (lv:LogLevel): Boolean = compareTo(lv) <= 0
  def >  (lv:LogLevel): Boolean = compareTo(lv) == 1
  def >= (lv:LogLevel): Boolean = compareTo(lv) >= 0
  def == (lv:LogLevel): Boolean = compareTo(lv) == 0
  def != (lv:LogLevel): Boolean = compareTo(lv) != 0

  def compareTo(lv:LogLevel) : Int = this.code.compareTo(lv.code)
}


case object Debug extends LogLevel("Debug", 1)
case object Info  extends LogLevel("Info" , 2)
case object Warn  extends LogLevel("Warn" , 3)
case object Error extends LogLevel("Error", 4)
case object Fatal extends LogLevel("Fatal", 5)