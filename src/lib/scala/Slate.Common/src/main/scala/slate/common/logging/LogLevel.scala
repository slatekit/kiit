package slate.common.logging

import slate.common.DateTime

/**
 * Created by kv on 10/14/2015.
 */
case class LogLevel( name:String, code:Int ){


  def <  (lv:LogLevel): Boolean = compareTo(lv) == -1
  def <= (lv:LogLevel): Boolean = compareTo(lv) <= 0
  def >  (lv:LogLevel): Boolean = compareTo(lv) == 1
  def >= (lv:LogLevel): Boolean = compareTo(lv) >= 0
  def == (lv:LogLevel): Boolean = compareTo(lv) == 0
  def != (lv:LogLevel): Boolean = compareTo(lv) != 0

  def compareTo(lv:LogLevel) : Int = this.code.compareTo(lv.code)
}


object LogLevel {
  val Debug = new LogLevel("Debug", 1)
  val Info  = new LogLevel("Info" , 2)
  val Warn  = new LogLevel("Warn" , 3)
  val Error = new LogLevel("Error", 4)
  val Fatal = new LogLevel("Fatal", 5)
}
