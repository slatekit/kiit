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
package slate.common

import slate.common.Reflector._

import scala.reflect.runtime._
import scala.reflect.runtime.universe.{TypeTag,Type,typeOf}

/**
  * Initialize with the class type
  *
  * @tparam T : The class type
  */
class Reflected[T:TypeTag]() {

  /**
    * the class type
    */
  val tpe:Type = typeOf[T]


  /**
    * the name of the class e.g. "User"
    *
    * @return
    */
  def name:String = tpe.typeSymbol.asClass.name.toString


  /**
    * the full name of the class including package e..g "App.Core.Users"
    *
    * @return
    */
  def fullname:String = tpe.typeSymbol.asClass.fullName


  /**
    * Creates an instance ( assumes existance of a 0 param constructor )
    * Only works for non-inner classes and for types with 0 parameter constructors.
    *
    * @return
    */
  def create() : T = Reflector.createInstance(tpe).asInstanceOf[T]


  /**
    * Creates an instance of the type dynamically using the parameters supplied.
    *
    * @param tpe
    * @return
    */
  def create(tpe:Type, args:Seq[_]): T = Reflector.createInstance(tpe, Option(args)).asInstanceOf[T]


  /**
    * Gets a field value from the instance
    * @param item: The instance to get the field value from
    * @param fieldName: The name of the field to get
    * @return
    */
  def getValue(item:Any, fieldName:String) : Any = Reflector.getFieldValue(item, fieldName)


  /**
    * Sets a field value in the instance
    *
    * @param item: The instance to set the field value to
    * @param fieldName: The name of the field to set
    * @param v
    */
  def setValue(item:Any, fieldName:String, v:Any) = Reflector.setFieldValue(item, fieldName, v)
}
