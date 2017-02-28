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
import scala.collection.mutable.{Map}
import slate.common.Funcs._


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
            tableName:String = "",
            private val _propList:Option[scala.collection.immutable.List[ModelField]] = None) {


  /**
   * The name of the table
   */
  val table = Strings.valueOrDefault(tableName, name)


  /**
   * The field that represents the id
   */
  val idField:Option[ModelField] = _propList.fold[Option[ModelField]](None)( props => {
    val items = props.filter( p => p.cat == "id")
    items.headOption
  })


  /**
   * The mapping of property names to the fields.
   */
  val _propMap = _propList.fold(Map[String, ModelField]())( props => {
    props.map( prop => prop.name -> prop )(collection.breakOut)
  })


  /**
   * whether there are any fields in the model
   * @return
   */
  def any: Boolean = size > 0


  /**
   * whether this model has an id field
   * @return
   */
  def hasId: Boolean = idField.isDefined


  /**
   * the number of fields in this model.
   * @return
   */
  def size: Int = _propList.fold(0)( props => props.size )


  /**
   * gets the list of fields in this model or returns an emptylist if none
   * @return
   */
  def fields:List[ModelField] = _propList.fold(List[ModelField]())( props => props )


  /**
   * builds a new model by adding an id field to the list of fields
   * @param name
   * @param dataType
   * @param autoIncrement
   * @return
   */
  def addId ( name:String, dataType:Type, autoIncrement:Boolean = false ) : Model =
  {
    addField(name, typeOf[Long], "", true, 0, 0, Some(name), Some(0), cat = "id")
  }


  /**
   * builds a new model by adding an text field to the list of fields
   * @param name
   * @param desc
   * @param isRequired
   * @param minLength
   * @param maxLength
   * @param storedName
   * @param defaultValue
   * @param tag
   * @param cat
   * @return
   */
  def addText( name:String, desc:String = "", isRequired:Boolean = false, minLength:Int = 0,
               maxLength:Int = 50,  storedName:Option[String] = None, defaultValue:String = "",
               tag:String = "", cat:String = "data"
             ): Model =
  {
    addField(name, typeOf[String], desc, isRequired, minLength, maxLength, storedName,
      Some(defaultValue), tag, cat)
  }


  /**
   * builds a new model by adding a bool field to the list of fields
   * @param name
   * @param desc
   * @param isRequired
   * @param storedName
   * @param tag
   * @param cat
   * @return
   */
  def addBool( name:String, desc:String = "", isRequired:Boolean = false,
               storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[String], desc, isRequired, 0, 0, storedName, Some(false), tag, cat)
  }


  /**
   * builds a new model by adding a date field to the list of fields
   * @param name
   * @param desc
   * @param isRequired
   * @param storedName
   * @param tag
   * @param cat
   * @return
   */
  def addDate( name:String, desc:String = "", isRequired:Boolean = false,
               storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[DateTime], desc, isRequired, 0, 0, storedName, Some(DateTime.min()), tag, cat)
  }


  /**
   * builds a new model by adding a short field to the list of fields.
   * @param name
   * @param desc
   * @param isRequired
   * @param storedName
   * @param tag
   * @param cat
   * @return
   */
  def addShort( name:String, desc:String = "", isRequired:Boolean = false,
              storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Short], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
  }


  /**
   * builds a new model by adding a new integer field to the list of fields.
   * @param name
   * @param desc
   * @param isRequired
   * @param storedName
   * @param tag
   * @param cat
   * @return
   */
  def addInt( name:String, desc:String = "", isRequired:Boolean = false,
              storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Int], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
  }


  /**
   * builds a new model by adding a new long field to the list of fields.
   * @param name
   * @param desc
   * @param isRequired
   * @param storedName
   * @param tag
   * @param cat
   * @return
   */
  def addLong( name:String, desc:String = "", isRequired:Boolean = false,
               storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Long], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
  }


  /**
   * builds a new model by adding a new double field to the list of fields.
   * @param name
   * @param desc
   * @param isRequired
   * @param storedName
   * @param tag
   * @param cat
   * @return
   */
  def addDouble( name:String, desc:String = "", isRequired:Boolean = false,
                 storedName:Option[String] = None, tag:String = "", cat:String = "data"): Model =
  {
    addField(name, typeOf[Double], desc, isRequired, 0, 0, storedName, Some(0), tag, cat)
  }


  /**
   * builds a new model by adding a new object field to the list of fields.
   * @param name
   * @param desc
   * @param isRequired
   * @param dataType
   * @param storedName
   * @param defaultValue
   * @return
   */
  def addObject( name:String, desc:String = "", isRequired:Boolean = false, dataType:Type,
                 storedName:Option[String] = None, defaultValue:Option[Any] = None): Model =
  {
    addField(name, dataType, desc, isRequired, 0, 0, storedName, defaultValue)
  }


  /**
   * builds a new model by adding a new field to the list of fields using the supplied fields.
   * @param name
   * @param dataType
   * @param desc
   * @param isRequired
   * @param minLength
   * @param maxLength
   * @param destName
   * @param defaultValue
   * @param tag
   * @param cat
   * @return
   */
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
               ) : Model = {
    val field = ModelField.build(name, dataType, desc, isRequired, minLength, maxLength,
      destName, defaultValue, tag, cat)
    add(field)
  }


  def add(field:ModelField): Model = {
    val newPropList = _propList.fold( List[ModelField](field))( props => {
      props :+ field
    })
    new Model(this.name, fullName, this.dataType, desc, table, Option(newPropList))
  }


  /**
   * Standardizes the model by adding all the slatekit standard entity fields which include
   * uniqueId, tag, timestamps, audit fields
   * @return
   */
  def standardize(): Model = {
    addText("uniqueId"   , isRequired = false, maxLength = 50  , tag = "standard", cat = "meta")
    addText("tag"        , isRequired = false, maxLength = 20  , tag = "standard", cat = "meta")
    addDate("createdAt" , isRequired = false, tag = "standard", cat = "meta")
    addInt ("createdBy" , isRequired = false, tag = "standard", cat = "meta")
    addDate("updatedAt" , isRequired = false, tag = "standard", cat = "meta")
    addInt ("updatedBy" , isRequired = false, tag = "standard", cat = "meta")
    this
  }


  /**
   * convenience method to generate a list of field definitions in javascript syntax
   * @return
   */
  def toJSFieldDefs(): String = {
    collect("", (field) => field.toJSDef())
  }


  /**
   * convenience method to generate a model instance in javascript with matching fields
   * @return
   */
  def toJSFieldInstances(): String = {
    collect("", (field) => field.toJSInstance())
  }


  /**
   * iterates over each model.
   * @param callback
   */
  def eachField(callback:(ModelField, Int) => Unit ):Unit = {
    _propList.map( props => {
      Option(callback).fold[Unit](Unit)( call => {
        props.indices.foreach( ndx => {
          val field = props(ndx)
          call(field, ndx)
        })
      })
      Unit
    })
  }


  def collect(start:String, callback:(ModelField) => String): String = {
    _propList.map( props => {
      props.indices.foldLeft(start)( (text,pos)  => {
        val field = props(pos)
        val sep = if(pos == 0 )Strings.newline() else "," + Strings.newline()
        val result = text + sep + callback(field)
        result
      })
    }).getOrElse(start)
  }
}
