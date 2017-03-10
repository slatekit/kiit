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

import slate.common.{DateTime, Inputs, Reflector}
import slate.common.reflect.ReflectConsts._
import scala.reflect.runtime.universe.{TypeTag, Type, typeOf}


/**
  * Initialize with the class type
  *
  */
class ReflectedClass(val tpe:Type){
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
   * Gets a field value from the instance
   * @param item: The instance to get the field value from
   * @param fieldName: The name of the field to get
   * @return
   */
  def getValue(item:Any, fieldName:String) : Any = Reflector.getFieldValue(item, fieldName)


  /**
   * Create an instance of the case class given inputs with inputs.
   * @param inputs
   * @return
   */
  def create(inputs:Inputs):Any = {

    // Get all declared fields in the case class
    val fields = Reflector.getFieldsByType(tpe)

    // Get the value of each field from the inputs.
    // NOTES:
    // 1. Parameter has to be in the inputs as case classes
    //    need to be created with all params dynamically.
    // 2. Supplying partial parameters can lead to issues
    //    So there is a requirement for all parameters
    val args = fields.map( field => {
      val dataType = field.typeSignature.resultType
      val fieldName = field.name.toString
      val dataValue = dataType match {
        case BoolType    => inputs.getBool  (fieldName)
        case IntType     => inputs.getInt   (fieldName)
        case LongType    => inputs.getLong  (fieldName)
        case FloatType   => inputs.getFloat (fieldName)
        case DoubleType  => inputs.getDouble(fieldName)
        case DateType    => inputs.getDate  (fieldName)
        case StringType  => inputs.getString(fieldName)
        case _           => inputs.getString(fieldName)
      }
      dataValue
    })

    // Finally create instance
    val inst = Reflector.createInstance(tpe, Some(args))
    inst
  }


  /**
   * Create an instance of the case class given inputs with inputs, but
   * defaults values for fields missing in the inputs
   * @param inputs
   * @return
   */
  def createWithDefaults(inputs:Inputs):Any = {

    // Get all declared fields in the case class
    val fields = Reflector.getFieldsByType(tpe)

    // Get the value of each field from the inputs.
    // NOTES:
    // 1. Parameter has to be in the inputs as case classes
    //    need to be created with all params dynamically.
    // 2. Supplying partial parameters can lead to issues
    //    So there is a requirement for all parameters
    val args = fields.map( field => {
      val dataType = field.typeSignature.resultType
      val fieldName = field.name.toString
      val dataValue = dataType match {
        case BoolType    => inputs.getBoolOrElse  (fieldName, false)
        case IntType     => inputs.getIntOrElse   (fieldName, 0)
        case LongType    => inputs.getLongOrElse  (fieldName, 0)
        case FloatType   => inputs.getFloatOrElse (fieldName, 0)
        case DoubleType  => inputs.getDoubleOrElse(fieldName, 0)
        case DateType    => inputs.getDateOrElse  (fieldName, DateTime.now())
        case StringType  => inputs.getStringOrElse(fieldName, "")
        case _           => inputs.getStringOrElse(fieldName, "")
      }
      dataValue
    })

    // Finally create instance
    val inst = Reflector.createInstance(tpe, Some(args))
    inst
  }


  /**
   * Create an instance of the case class given inputs with inputs.
   * @param inputs
   * @return
   */
  def updateVars(inputs:Inputs):Any = {
    val instance = Reflector.createInstance(tpe)

    // Get all declared fields in the case class
    val fields = Reflector.getFieldsByType(tpe)

    // Get the value of each field from the inputs.
    // NOTES:
    // 1. Parameter has to be in the inputs as case classes
    //    need to be created with all params dynamically.
    // 2. Supplying partial parameters can lead to issues
    //    So there is a requirement for all parameters
    fields.foreach( field => {
      val dataType = field.typeSignature.resultType
      val fieldName = field.name.toString
      val dataValue = dataType match {
        case BoolType    => inputs.getBool  (fieldName)
        case IntType     => inputs.getInt   (fieldName)
        case LongType    => inputs.getLong  (fieldName)
        case FloatType   => inputs.getFloat (fieldName)
        case DoubleType  => inputs.getDouble(fieldName)
        case DateType    => inputs.getDate  (fieldName)
        case StringType  => inputs.getString(fieldName)
        case _           => inputs.getString(fieldName)
      }
      Reflector.setFieldValue(instance, fieldName, dataValue)
    })
    instance
  }
}
