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


import scala.reflect.runtime.universe._
import scala.collection.mutable.{ListBuffer, Map}


/**
  * Stores the schema of a data-model with properties.
  * @param name     :
  * @param fullName :
  * @param dataType :
  */
class Model(val name:String,
            val fullName:String,
            val dataType:Option[Type] = None,
            val desc:String = "",
            var table:String = "" ) {

  protected val _propList = new ListBuffer[ModelField]()
  protected val _propMap = Map[String, ModelField]()
  protected var _idField:ModelField = null

  table = Strings.valueOrDefault(table, name)

  def addId ( name:String, dataType:Type, autoIncrement:Boolean = false ) : Model =
  {
    _idField = addField(name, typeOf[Long], "", true, 0, 0, Some(name), Some(0), cat = "id")
    this
  }


  def addText( name:String, desc:String = "", isRequired:Boolean = false, minLength:Int = 0,
               maxLength:Int = 50,  storedName:Option[String] = None, defaultValue:String = "",
               tag:String = "", cat:String = "data"
             ): Model =
  {
    addField(name, typeOf[String], desc, isRequired, minLength, maxLength, storedName,
      Some(defaultValue), tag, cat)
    this
  }


  def addBool( name:String, desc:String = "", isRequired:Boolean = false,
               storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[String], desc, isRequired, 0, 0, storedName, Some(false), tag, cat)
    this
  }


  def addDate( name:String, desc:String = "", isRequired:Boolean = false,
               storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[DateTime], desc, isRequired, 0, 0, storedName, Some(DateTime.min()), tag, cat)
    this
  }


  def addShort( name:String, desc:String = "", isRequired:Boolean = false,
              storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Short], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
    this
  }


  def addInt( name:String, desc:String = "", isRequired:Boolean = false,
              storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Int], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
    this
  }


  def addLong( name:String, desc:String = "", isRequired:Boolean = false,
               storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Long], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
    this
  }


  def addDouble( name:String, desc:String = "", isRequired:Boolean = false,
                 storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Double], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
    this
  }


  def addObject( name:String, desc:String = "", isRequired:Boolean = false, dataType:Type,
                 storedName:Option[String] = None, defaultValue:Option[Any] = None): Model =
  {
    addField(name, dataType, desc, isRequired, 0, 0, storedName, defaultValue)
    this
  }


  def addField(
                  name:String,
                  dataType:Type,
                  desc:String = "",
                  isRequired:Boolean = false,
                  minLength:Int = -1,
                  maxLength:Int = -1,
                  destName:Option[String] = None,
                  defaultValue:Option[Any] = None,
                  tag:String = "",
                  cat:String = "data"
               ) : ModelField =
  {
    var finalName = name
    if(destName.nonEmpty)
    {
      if(destName.get.trim != "" )
      {
        finalName = destName.get
      }
    }


    val field = new ModelField(name = name, desc = desc, dataType = dataType,
      storedName = finalName, pos = 0,
      isRequired = isRequired, minLength = minLength, maxLength = maxLength,
      defaultValue, "", tag = tag, cat = cat)
    _propList += field
    _propMap += ( finalName -> field)
    field
  }


  def fields:ListBuffer[ModelField] =
  {
    _propList
  }


  def anyFields(): Boolean =
  {
    _propList.size > 0
  }


  def hasId(): Boolean =
  {
    _idField != null
  }


  def idField(): ModelField =
  {
    _idField
  }


  def totalFields(): Int =
  {
    _propList.size
  }


  def standardize(): Model = {
    addText("uniqueId"   , isRequired = false, maxLength = 50  , tag = "standard", cat = "meta")
    addText("tag"        , isRequired = false, maxLength = 20  , tag = "standard", cat = "meta")
    addDate("created_at" , isRequired = false, tag = "standard", cat = "meta")
    addInt ("created_by" , isRequired = false, tag = "standard", cat = "meta")
    addDate("updated_at" , isRequired = false, tag = "standard", cat = "meta")
    addInt ("updated_by" , isRequired = false, tag = "standard", cat = "meta")
    this
  }


  def toJSFieldDefs(): String = {
    var txt = ""
    eachField( (field, pos) => {
      val sep = if(pos == 0 )Strings.newline() else "," + Strings.newline()
      txt = txt + sep + field.toJSDef()
    })
    txt
  }


  def toJSFieldInstances(): String = {
    var txt = ""
    eachField( (field, pos) => {
      val sep = if(pos == 0 ) Strings.newline() else "," + Strings.newline()
      txt = txt + sep + field.toJSInstance()
    })
    txt
  }


  def eachField(callback:(ModelField, Int) => Unit ):Unit = {
    var pos = 0
    for(field <- _propList){
      callback(field, pos)
      pos = pos + 1
    }
  }
}
