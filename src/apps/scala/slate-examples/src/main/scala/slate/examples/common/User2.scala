/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.examples.common


// 1. You can declare class members in the constructor!
// 2. You can declare set the access modifiers ( none = public, protected, private )
// 3. You can declare whether they are mutable ( var ) or immutable ( val )
class User2( val name:String,
             var isActive:Boolean,
             protected val email:String,
             private val account:Int

           )
{

  // Overloaded constructors must call the primary constructor first
  def this() = {
    this("", false, "", 0)
  }


  // Overloaded constructors must call the primary constructor first
  def this(name:String) =
  {
    this(name, false, "", 0)
  }


  // YOu can start executing code after the primary constructor is called.
  println("instantiated with : " + name)


  def printInfo(): Unit = {
    println( name + ", " + email + ", " + account + ", " + isActive )
  }
}


// CASE Classes
// 1. members in constructor are immutable by default
// 2. copy support built-in
// 3. useful for pattern-matching ( explained later )
case class User3( name:String,
                  isActive:Boolean,
                  email:String,
                  account:Int
                )
{

  // Overloaded constructors must call the primary constructor first
  def this() = {
    this("", false, "", 0)
  }


  // Overloaded constructors must call the primary constructor first
  def this(name:String) =
  {
    this(name, false, "", 0)
  }
}


// Traits are interfaces with implementations
// You can compose your clases to extend the trait.
trait LogTrait {

  protected def debug(msg:String):Unit = {
    log("debug", msg)
  }


  def info(msg:String):Unit = {
    log("info", msg)
  }


  def error(msg:String):Unit = {
    log("error", msg)
  }


  private def log(level:String, msg:String):Unit = {
    println( s"${level} : ${msg}" )
  }
}


// Easy to now extend your class using the trait
// Traits provide a way to compose/attach behaviour to your class easily
class MyService1 extends LogTrait {

  def process():Unit = {
    debug("my service 1")
    info("starting")
    error("error!")
  }
}


// Easy to now extend your class using the trait
// Traits provide a way to compose/attach behaviour to your class easily
class MyService2 {

  def process():Unit = {
    val svc1 = new MyService1()

    // Compile error ( protected )
    //svc1.debug("my service 2")
    svc1.info("starting")
    svc1.error("error!")
  }
}


