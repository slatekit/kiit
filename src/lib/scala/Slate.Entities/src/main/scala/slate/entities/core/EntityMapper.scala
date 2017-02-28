/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
  */

package slate.entities.core

import slate.common.mapper.Mapper
import slate.common.query.QueryEncoder
import slate.common.{DateTime, Model, Reflector, Strings}

import scala.reflect.runtime.universe._


/**
  * Maps an entity to sql and from sql records.
  *
  * @param model
  */
class EntityMapper(model:Model) extends Mapper(model) {

  def mapToSql(item:AnyRef, update:Boolean, fullSql:Boolean = false): String = {

    if (!_model.any)
      Strings.empty
    else
      mapFields(item, update, fullSql)
  }


  private def mapFields(item:AnyRef, update:Boolean, fullSql:Boolean = false): String = {
    var dat = ""
    var sql = ""
    var cols = ""


    val len = _model.fields.size
    for( ndx <- 0 until len)
    {
      val mapping = _model.fields(ndx)
      val propName = mapping.name
      val colName = getColumnName(mapping.storedName)
      val include = !Strings.isMatch(propName, "id")

      if(include)
      {
        val data = if( mapping.dataType == _tpeString )
        {
          val sVal = Reflector.getFieldValue(item, mapping.name).asInstanceOf[String]
          val sValFinal = Strings.valueOrDefault(sVal, "")
         "'" + QueryEncoder.ensureValue(sValFinal) + "'"
        }
        else if(mapping.dataType == typeOf[Boolean])
        {
          val bVal = Reflector.getFieldValue(item, mapping.name).asInstanceOf[Boolean]
          if(bVal)  "1" else "0"
        }
        else if(mapping.dataType == typeOf[Int])
        {
          val iVal = Reflector.getFieldValue(item, mapping.name).asInstanceOf[Int]
          iVal.toString
        }
        else if(mapping.dataType == typeOf[Short])
        {
          val iVal = Reflector.getFieldValue(item, mapping.name).asInstanceOf[Short]
          iVal.toString
        }
        else if(mapping.dataType == typeOf[Long])
        {
          val lVal = Reflector.getFieldValue(item, mapping.name).asInstanceOf[Long]
          lVal.toString
        }
        else if(mapping.dataType == typeOf[Double])
        {
          val dVal = Reflector.getFieldValue(item, mapping.name).asInstanceOf[Double]
          dVal.toString
        }
        else if(mapping.dataType == typeOf[DateTime])
        {
          val dtVal = Reflector.getFieldValue(item, mapping.name).asInstanceOf[DateTime]
          "'" + dtVal.toStringMySql() + "'"
        }
        else // Object
        {
          val objVal = Reflector.getFieldValue(item, mapping.name)
          val data = Option(objVal).fold("")( obj => obj.toString())
          "'" + QueryEncoder.ensureValue(data) + "'"
        }

        // Setup the inserts
        val isLastField = ndx == _model.fields.size - 1
        if(!update)
        {
          cols += colName
          dat += data
          if(!isLastField)
          {
            cols += ","
            dat += ","
          }
        }
        else
        {
          sql += colName + "=" + data
          if(!isLastField)
          {
            sql += ","
          }
        }
      }
    }

    if(update)
    {
      sql = " " + sql
    }
    else
    {
      cols = "(" + cols + ") "
      sql = cols + "VALUES (" + dat + ")"
    }
    if(!fullSql)
      sql
    else if(update)
      s"update ${_model.name} set " + sql + ";"
    else
      s"insert into ${_model.name} " + sql + ";"
  }


  def getColumnName(name:String) : String =
  {
    "`" + name + "`"
  }
}
