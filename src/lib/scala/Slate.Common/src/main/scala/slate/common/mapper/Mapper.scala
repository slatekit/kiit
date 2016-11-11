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

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._
import slate.common.Field
import slate.common._


class Mapper(protected val _model:Model) {

  protected var _tpeString:Type = Reflector.getFieldType(typeOf[Temp], "typeString")

  def model() : Model = _model


  def init(): Unit =
  {
  }


  def createEntity() : Any =
  {
    Reflector.createInstance(_model.dataType.get)
  }


  def mapFrom(record:MappedSourceReader): Option[Any] =
  {
    if(!_model.any)
      return None

    // NOTE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // Not using pattern matching here on the types.
    // This is because using "if ( datatype == typeOf[x] )"
    // is slightly faster
    val entity:Any = createEntity()
    for( ndx <- 0 until _model.fields.size)
    {
      val mapping = _model.fields(ndx)
      val colName = mapping.storedName
      val fieldType = mapping.dataType

      if( mapping.dataType == _tpeString )
      {
        val sVal = record.get(colName)
        Reflector.setFieldValue(entity, mapping.name, sVal)
      }
      else if(mapping.dataType == typeOf[Boolean])
      {
        val bVal = record.getBool(colName)
        Reflector.setFieldValue(entity, mapping.name, bVal)
      }
      else if(mapping.dataType == typeOf[Int])
      {
        val iVal = record.getInt(colName)
        Reflector.setFieldValue(entity, mapping.name, iVal)
      }
      else if(mapping.dataType == typeOf[Short])
      {
        val iVal = record.getShort(colName)
        Reflector.setFieldValue(entity, mapping.name, iVal)
      }
      else if(mapping.dataType == typeOf[Long])
      {
        val lVal = record.getLong(colName)
        Reflector.setFieldValue(entity, mapping.name, lVal)
      }
      else if(mapping.dataType == typeOf[Double])
      {
        val dVal = record.getDouble(colName)
        Reflector.setFieldValue(entity, mapping.name, dVal)
      }
      else if(mapping.dataType == typeOf[DateTime])
      {
        val dVal = record.getDate(colName)
        Reflector.setFieldValue(entity, mapping.name, dVal)
      }
    }
    Some(entity)
  }


  override def toString(): String =
  {
    Ensure.isNotNull(_model, "Model and/or schema not initialized")

    var all = ""
    for(field <- _model.fields)
    {
      all += field.toString() + "\n"
    }
    all
  }
}


object Mapper {



  def loadSchema(item:AnyRef, dataType:Type):Model =
  {
    val modelName = item.getClass().getSimpleName
    val modelNameFull = item.getClass.getName

    // Now add all the fields.
    val matchedFieldsR = Reflector.getFieldsWithAnnotations(item, dataType, typeOf[Field])
    val matchedFields = matchedFieldsR.reverse
    val fieldId = ModelField.id( name = "id" , autoIncrement = true, dataType = typeOf[Long])
    val fields = new ListBuffer[ModelField]()
    fields.append(fieldId)

    // Loop through each field
    for(matchedField <- matchedFields )
    {
      val anno     = matchedField._4.asInstanceOf[Field]
      val name     = matchedField._1
      val required = anno.required
      val length   = anno.length
      val dataType = matchedField._5
      fields.append( ModelField.build( name= name, dataType= dataType, isRequired= required, maxLength = length ) )
    }

    val model = new Model(modelName, modelNameFull, Some(dataType), _propList = Some(fields.toList))
    model
  }
}
