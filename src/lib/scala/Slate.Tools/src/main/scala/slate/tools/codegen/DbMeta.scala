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

package slate.tools.codegen

import slate.common.databases.{Db, DbConString, DbField, DbTableMapper}
import slate.common.{Model, Reflector}

import scala.reflect.runtime.universe._

class DbMeta(private val con:DbConString) {

  private val fieldModel = asModel()


  def getTableAsModel(tableName:String):Model = {
    val db = new Db(con).open()
    val sql = s"show fields from `${tableName}`;"
    val mapper = new DbTableMapper(fieldModel)
    val result = db.mapMany(sql, mapper)
    if(result.isDefined){
      val model = new Model(tableName, tableName)
      for(item <- result.get.asInstanceOf[List[Option[Any]]]){
        val rec = item.asInstanceOf[Option[Any]].get.asInstanceOf[DbField]
        if(rec.isKey){
          model.addId("id", autoIncrement = true, dataType = typeOf[Int])
        }
        else {
          val cat = rec.name match {
            case "uniqueId"    => "meta"
            case "tag"         => "meta"
            case "createdAt"   => "meta"
            case "createdBy"   => "meta"
            case "updatedAt"   => "meta"
            case "updatedBy"   => "meta"
            case _             => "data"
          }
          model.addField(rec.name, rec.getFieldType(), "", isRequired = !rec.isNull, maxLength = rec.maxLength(), destName = Some(rec.name), cat = cat)
        }
      }
      if(model.idField.isEmpty){
        model.addId("id", autoIncrement = true, dataType = typeOf[Int])
      }
      return model
    }
    null
  }


  def asModel():Model = {
    val stringType = Reflector.getFieldTypeString()
    // CASE 1: specify the name of the model e.g. "Resource"
    val model = new Model("DbField", "slate.common.databases.DbField", Some(typeOf[DbField]))

    // CASE 3: add fields for text, bool, int, date etc.
    model.addField(name = "name"       , dataType = stringType, isRequired = true, maxLength = 30, destName = Some("field")  )
    model.addField(name = "dataType"   , dataType = stringType, isRequired = true, maxLength = 30, destName = Some("type")   )
    model.addField(name = "nullable"   , dataType = stringType, isRequired = true, maxLength = 30, destName = Some("null")   )
    model.addField(name = "key"        , dataType = stringType, isRequired = true, maxLength = 30, destName = Some("key")    )
    model.addField(name = "defaultVal" , dataType = stringType, isRequired = true, maxLength = 30, destName = Some("default"))
    model.addField(name = "extra"      , dataType = stringType, isRequired = true, maxLength = 30, destName = Some("extra")  )
    model
  }
}
