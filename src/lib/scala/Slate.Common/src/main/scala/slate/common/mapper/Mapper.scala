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

package slate.common.mapper


import slate.common.utils.Temp

import scala.reflect.runtime.universe._
import slate.common.Field
import slate.common._


/**
 * A mapper that can create a model from a source reader ( which can be a JDBC record set )
 * NOTES:
 * 1. can create a model that is a case class
 * 2. can create a model that is a regular class
 * @param _model
 */
class Mapper(protected val _model:Model) {

  protected val _tpeString:Type = Reflector.getFieldType(typeOf[Temp], "typeString")


  /**
    * The model associated with this mapper.
    * @return
    */
  def model() : Model = _model


  /**
    * Creates the entity/model expecting a 0 parameter constructor
    * @return
    */
  def createEntity() : AnyRef =
    Reflector.createInstance(_model.dataType.get)


  /**
    * Creates the entity/model with all the supplied constructor parameters (ideal for case classes)
    * @param args
    * @return
    */
  def createEntityWithArgs(args:Option[Seq[Any]]) : AnyRef =
    Reflector.createInstance(_model.dataType.get, args)


  def copyWithId[T](id:Long, entity:T): T  = {
    entity
  }


  /**
    * Maps all the parameters to a class that takes in all parameters in the constructor
    * This is ideally for Case Classes, allowing the representation of models as immutable
    * case classes
    * @param record
    * @return
    */
  def mapFrom(record:MappedSourceReader): Option[Any] =
  {
    if(_model.any) {
      _model.dataType.fold[Option[Any]](None)( tpe => {
        if ( Reflector.isCaseClass(tpe) ){
          mapFromToValType(record)
        }
        else
          mapFromToVarType(record)
      })
    }
    else
      None
  }


  /**
    * Maps all the parameters to a class that takes in all parameters in the constructor
    * This is ideally for Case Classes, allowing the representation of models as immutable
    * case classes
    * @param record
    * @return
    */
  def mapFromToValType(record:MappedSourceReader): Option[Any] =
  {
    if(_model.any) {

      // NOTE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // 1. Not using pattern matching here on the types for
      //    slightly better performance.
      val data = _model.fields.map(mapping => {
        val colName = mapping.storedName
        val fieldType = mapping.dataType

        if (fieldType == _tpeString) {
          val sVal = record.get(colName)
          sVal
        }
        else if (fieldType == typeOf[Boolean]) {
          val bVal = record.getBool(colName)
          bVal
        }
        else if (fieldType == typeOf[Int]) {
          val iVal = record.getInt(colName)
          iVal
        }
        else if (fieldType == typeOf[Short]) {
          val iVal = record.getShort(colName)
          iVal
        }
        else if (fieldType == typeOf[Long]) {
          val lVal = record.getLong(colName)
          lVal
        }
        else if (fieldType == typeOf[Double]) {
          val dVal = record.getDouble(colName)
          dVal
        }
        else if (fieldType == typeOf[DateTime]) {
          val dVal = record.getDate(colName)
          dVal
        }
        else {
          val sVal = record.get(colName)
          sVal
        }
      })
      val entity = createEntityWithArgs(Some(data))
      Some(entity)
    }
    else
      None
  }

  /**
    * Maps all the parameters to a class that supports vars as fields.
    * While this is NOT recommended, it is still supported.
    * case classes
    * @param record
    * @return
    */
  def mapFromToVarType(record:MappedSourceReader): Option[Any] =
  {
    if(_model.any) {

      // NOTE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // 1. Not using pattern matching here on the types for
      //    slightly better performance.
      val entity: Any = createEntity()
      _model.fields.foreach(mapping => {
        val colName = mapping.storedName
        val fieldType = mapping.dataType

        if (fieldType == _tpeString) {
          val sVal = record.get(colName)
          Reflector.setFieldValue(entity, mapping.name, sVal)
        }
        else if (fieldType == typeOf[Boolean]) {
          val bVal = record.getBool(colName)
          Reflector.setFieldValue(entity, mapping.name, bVal)
        }
        else if (fieldType == typeOf[Int]) {
          val iVal = record.getInt(colName)
          Reflector.setFieldValue(entity, mapping.name, iVal)
        }
        else if (fieldType == typeOf[Short]) {
          val iVal = record.getShort(colName)
          Reflector.setFieldValue(entity, mapping.name, iVal)
        }
        else if (fieldType == typeOf[Long]) {
          val lVal = record.getLong(colName)
          Reflector.setFieldValue(entity, mapping.name, lVal)
        }
        else if (fieldType == typeOf[Double]) {
          val dVal = record.getDouble(colName)
          Reflector.setFieldValue(entity, mapping.name, dVal)
        }
        else if (fieldType == typeOf[DateTime]) {
          val dVal = record.getDate(colName)
          Reflector.setFieldValue(entity, mapping.name, dVal)
        }
        else {
          if(_tpeString.toString == fieldType.toString){
            val sVal = record.get(colName)
            Reflector.setFieldValue(entity, mapping.name, sVal)
          }
        }
      })
      Some(entity)
    }
    else
      None
  }


  override def toString(): String =
  {
    val all = _model.fields.foldLeft("")( (s, field) => s + field.toString() + Strings.newline())
    all
  }
}


object Mapper {

  /**
    * Builds a schema ( Model ) from the Class/Type supplied.
    * NOTE: The mapper then works off the Model class for to/from mapping of data to model.
    * @param dataType
    * @return
    */
  def loadSchema(dataType:Type):Model =
  {
    val modelName = dataType.typeSymbol.asClass.name.toString
    val modelNameFull = dataType.typeSymbol.asClass.fullName

    // Now add all the fields.
    val matchedFieldsR = Reflector.getFieldsWithAnnotations(None, dataType, typeOf[Field])
    val matchedFields = matchedFieldsR.reverse
    val fieldId = ModelField.id( name = "id" , autoIncrement = true, dataType = typeOf[Long])
    val fields = new scala.collection.mutable.ListBuffer[ModelField]()
    fields.append(fieldId)

    // Loop through each field
    matchedFields.foreach( matchedField => {
      if(matchedField._1 != "id") {
        val anno = matchedField._4.asInstanceOf[Field]
        val name = matchedField._1
        val required = anno.required
        val length = anno.length
        val dataType = matchedField._5
        fields.append(ModelField.build(name = name, dataType = dataType, isRequired = required,
          maxLength = length))
      }
    })

    val model = new Model(modelName, modelNameFull, Some(dataType), _propList = Some(fields.toList))
    model
  }
}
