/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common

object Funcs {


  def defaultOrExecute[T](condition:Boolean, defaultValue:T, f: => T): T = {
    if(condition){
      return defaultValue
    }
    f
  }


  def execute[T](data:Option[T], f:(T) => Unit): Unit = {
    data.fold(Unit)( item => {
      f(item)
      Unit
    })
  }


  def executeIf[T](condition:Boolean, data:Option[T], f:(T) => Unit): Unit = {
    if(condition) {
      data.fold(Unit)(item => {
        f(item)
        Unit
      })
    }
  }


  /**
   * loops through the list with support for breaking the loop early
   * @param args    : The list to iterate through
   * @param callback: The callback to call
   * @param start   : The index to the start with
   */
  def loop(args:Seq[Any], start:Int, callback:(Int) => Boolean):Unit =
  {
    var loop = true
    var ndx = start
    if (args == null || args.size == 0 || ndx >= args.size)
      return

    while (loop && ndx < args.length)
    {
      loop = callback(ndx)
      ndx = ndx + 1
    }
  }


  def getStringByOrder(key:String, f1:String=>Option[String], f2:String => Option[String]): String = {
    val result1 = f1(key)

    // f1 => none ? then try f2
    val finalResult = result1.fold[String]( f2(key).getOrElse("") ) ( res1Value => {

      // check f1 value for empty string value
      val f1Orf2Value = if (Strings.isNullOrEmpty(res1Value)) f2(key).getOrElse("") else res1Value

      f1Orf2Value
    })
    finalResult
  }


  def getStringByOrderOrElse(key:String,
                             f1:String=>Option[String],
                             f2:String => Option[String],
                             defaultValue:String ): String = {
    val result1 = f1(key)

    // f1 => none ? then try f2
    val finalResult = result1.fold[String]( f2(key).getOrElse(defaultValue) ) ( res1Value => {

      // check f1 value for empty string value
      val f1Orf2Value =
        if (Strings.isNullOrEmpty(res1Value)) {
        f2(key).getOrElse(defaultValue)
      }
      else {
        res1Value
      }
      f1Orf2Value
    })
    finalResult
  }
}
