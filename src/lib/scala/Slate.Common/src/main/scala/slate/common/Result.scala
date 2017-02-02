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

import slate.common.results.{ ResultChecks}
import slate.common.serialization.{ObjectBuilderJson, SerializerJson}


/**
  * Container for a Success/Failure value of type T with additional values to represent
  * a string message, code, tag, error and more.
  *
  * @param success : True / False success flag
  * @param code    : Numeric status code
  * @param msg     : Optional string message for more information
  * @param err     : Optional exception
  * @param ext     : Optional extra data / metadata
  * @param tag     : Optional tag used for tracking purposes
  * @param ref     : Optional reference to some original value
  * @tparam T      : Type T
  */
sealed abstract class Result[+T](
                                    val success : Boolean                 ,
                                    val code    : Int               = 0   ,
                                    val msg     : Option[String]    = None,
                                    val err     : Option[Throwable] = None,
                                    val ext     : Option[Any]       = None,
                                    val tag     : Option[String]    = None,
                                    val ref     : Option[Any]       = None
                                 ) extends Product with ResultChecks
{

  def failure: Boolean = !success


  def isEmpty: Boolean


  def isDefined: Boolean = !isEmpty


  def get: T


  def getOrElse[V >: T]( default: => V ): V =
    if (isEmpty) default else this.get


  /** If the option is nonempty, return a function applied to its value,
    *  wrapped in a Some i.e. <code>Some(f(this.get))</code>.
    *  Otherwise return <code>None</code>.
    *
    *  @param  f   the function to apply
    */
  def map[B](f: T => B): Result[B] =
    if (isEmpty) this.asInstanceOf[Result[B]]
    else SuccessResult[B](f(this.get), code, msg, ext, tag)


  /** If the option is nonempty, return a function applied to its value,
    *  wrapped in a Some i.e. <code>Some(f(this.get))</code>.
    *  Otherwise return <code>defaultValue</code>.
    *
    *  @param  f   the function to apply
    */
  def mapOrElse[V](defaultValue:V, f: T => V): V =
    if (isEmpty) defaultValue
    else f(this.get)


  /** If the result is nonempty, return a function applied to its value.
    *  Otherwise return None.
    *
    *  @param  f   the function to apply
    */
  def flatMap[B](f: T => Result[B]): Result[B] =
    if (isEmpty) this.asInstanceOf[Result[B]]
    else f(this.get)


  def and[V >: T](other:Result[V]):Result[V] = {
    if(this.success && other.success){
      this.asInstanceOf[Result[V]]
    }
    else if(!this.success){
      this.asInstanceOf[Result[V]]
    }
    else
      other
  }


  def print():Unit = {
    println("success : " + success           )
    println("code    : " + code              )
    println("data    : " + (if(isEmpty) "Nothing" else getOrElse("null") ))
    println("msg     : " + msg               )
    println("err     : " + err               )
    println("ext     : " + ext               )
    println("tag     : " + tag               )
    println("type    : " + ( if(isEmpty) "Nothing" else get.getClass.getName ))
  }

  override def statusCode = code
}

/**
  * The success branch of the Result[T]
  * @param value   : The value for a successful result/action
  * @param code    : Numeric status code
  * @param msg     : Optional string message for more information
  * @param ext     : Optional extra data / metadata
  * @param tag     : Optional tag used for tracking purposes
  * @tparam T
  */
final case class SuccessResult[+T](
                                    value               : T                       ,
                                    override val code   : Int                     ,
                                    override val msg    : Option[String]    = None,
                                    override val ext    : Option[Any]       = None,
                                    override val tag    : Option[String]    = None,
                                    override val ref    : Option[Any]       = None
                                  ) extends Result[T](true, code, msg, None, ext, tag, ref) {
   def isEmpty = false

   def get = value
 }


/**
  * The failure branch of the Result
  * @param value   : The value
  * @param code    : Numeric status code
  * @param msg     : Optional string message for more information
  * @param err     : Optional exception
  * @param ext     : Optional extra data / metadata
  * @param tag     : Optional tag used for tracking purposes
  * @tparam T
  */
final case class FailureResult[+T](
                                    value               : Option[T]         = None,
                                    override val code   : Int               = 0   ,
                                    override val msg    : Option[String]    = None,
                                    override val err    : Option[Throwable] = None,
                                    override val ext    : Option[Any]       = None,
                                    override val tag    : Option[String]    = None,
                                    override val ref    : Option[Any]       = None
                            )       extends Result[T](false, code, msg, err, ext, tag, ref) {
  def isEmpty = true

  def get = value.get
}


case object NoResult extends Result[Nothing](false, 0) {
  def isEmpty = true

  def get = throw new NoSuchElementException("NoResult.get")
}

