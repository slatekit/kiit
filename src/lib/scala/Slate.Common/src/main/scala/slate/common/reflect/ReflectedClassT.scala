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
package slate.common.reflect

import slate.common.Reflector

import scala.reflect.runtime.universe.{Type, TypeTag, typeOf}

/**
  * Initialize with the class type
  *
  * @tparam T : The class type
  */
class ReflectedClassT[T:TypeTag]() extends ReflectedClass(typeOf[T]) {

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
    * Sets a field value in the instance
    * Note: This is not used internally in slatekit as there are no var properties.
    * However, this is here for convenience ( also useful for unit-tests, mockups, prototypes )
    * @param item: The instance to set the field value to
    * @param fieldName: The name of the field to set
    * @param v
    */
  def setValue(item:Any, fieldName:String, v:Any) = Reflector.setFieldValue(item, fieldName, v)
}
