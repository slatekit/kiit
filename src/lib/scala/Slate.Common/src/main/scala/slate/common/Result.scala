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


/**
  * Container for a Success/Failure value of type T with additional values to represent
  * a string message, code, tag, error and more.
  *
  * @tparam T      : Type T
  */
sealed abstract class Result[+T] extends Product with ResultChecks
{
  val code    : Int
  val msg     : Option[String]
  val err     : Option[Throwable]
  val tag     : Option[String]
  val ref     : Option[Any]


  def success: Boolean


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
    else SuccessResult[B](f(this.get), code, msg, tag, ref)


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


  /** If the result is nonempty, return a function applied to its value.
    *  Otherwise return None.
    *
    *  @param  f   the function to apply
    */
  def fold[B](ifEmpty: => B)(f: T => B): B =
    if (isEmpty) ifEmpty else f(this.get)


  def toOption: Option[T] = {
    if (isEmpty) None
    else Some(this.get)
  }


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

  override def statusCode = code
}

/**
  * The success branch of the Result[T]
  * @param value   : The value for a successful result/action
  * @param code    : Numeric status code
  * @param msg     : Optional string message for more information
  * @param tag     : Optional tag used for tracking purposes
  * @param ref     : Optional reference to some object ( intentional set as Any for now )
  * @param err     : Optional exception
  * @tparam T
  */
final case class SuccessResult[+T](
                                    value  : T                       ,
                                    code   : Int                     ,
                                    msg    : Option[String]    = None,
                                    tag    : Option[String]    = None,
                                    ref    : Option[Any]       = None,
                                    err    : Option[Throwable] = None
                                  ) extends Result[T] {

   def isEmpty = false


   def get = value


   def success:Boolean = true
}


/**
  * The failure branch of the Result
  * @param code    : Numeric status code
  * @param msg     : Optional string message for more information
  * @param err     : Optional exception
  * @param tag     : Optional tag used for tracking purposes
  * @param ref     : Optional reference to some object ( intentional set as Any for now )
  * @tparam T
  */
final case class FailureResult[+T](
                                    code   : Int               = 0   ,
                                    msg    : Option[String]    = None,
                                    err    : Option[Throwable] = None,
                                    tag    : Option[String]    = None,
                                    ref    : Option[Any]       = None
                                  ) extends Result[T] {
  def isEmpty:Boolean = true


  def success:Boolean = false


  def get = throw new NoSuchElementException("NoResult.get")
}


case object NoResult extends Result[Nothing] {
  val code    : Int               = 0
  val msg     : Option[String]    = None
  val err     : Option[Throwable] = None
  val tag     : Option[String]    = None
  val ref     : Option[Any]       = None

  def isEmpty = true

  def success:Boolean = false

  def get = throw new NoSuchElementException("NoResult.get")
}

