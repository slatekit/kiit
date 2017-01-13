/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common
import scala.reflect.runtime.universe._
/**
  * Super minimal "Service locator" for use in the AppContext
  * for allowing any application to get access on registered services
  *
  */
class IocRunTime() {

  private val _lookup = scala.collection.mutable.Map[String, Any]()

  /**
    * Registers a new service at runtime
    *
    * NOTE: !!!!!!!!
    * This allows for mutability by design.
    * It is required as the registration / initialization of some services ( e.g. AWS S3, SQS )
    * are delayed until after startup and can also be loaded dynamically by modules.
    *
    * @return
    */
  def register(value:Any):IocRunTime = {
    val key = value.getClass.getTypeName.replaceAllLiterally("$", ".")
    _lookup(key) = value
    this
  }


  /**
    * Registers a new service at runtime
    *
    * NOTE: !!!!!!!!
    * This allows for mutability by design.
    * It is required as the registration of some services ( e.g. AWS S3, SQS )
    * are delayed until after startup and can also be loaded dynamically by modules.
    *
    * @param key
    * @return
    */
  def register(key:String, value:Any):IocRunTime = {
    _lookup(key) = value
    this
  }


  /**
    * whether this container contains the service associated with this key
    * @param key
    * @return
    */
  def contains(key:String):Boolean = {
    val finalKey = if(key.contains("$")) key.replaceAllLiterally("$", ".") else key
    _lookup.contains(finalKey)
  }


  /**
    * gets the service with the supplied key
    * @tparam T
    * @return
    */
  def getAs[T:TypeTag]():Option[T] = {
    val key = typeOf[T].typeSymbol.asType.fullName
    if(_lookup.contains(key))
      Some(_lookup(key).asInstanceOf[T])
    else
      None
  }


  /**
    * gets the service with the supplied key
    * @param key
    * @tparam T
    * @return
    */
  def get[T](key:String):Option[T] = {
    if(_lookup.contains(key))
      Some(_lookup(key).asInstanceOf[T])
    else
      None
  }
}
