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


case class ModelField (
                         name: String            = "",
                         desc:String             = "",
                         dataType:Type           = typeOf[String],
                         storedName:String       = "",
                         pos:Int                 = 0,
                         isRequired:Boolean      = true,
                         minLength:Int           = -1,
                         maxLength:Int           = -1,
                         defaultVal:Option[Any]  = None,
                         key:String              = "",
                         extra:String            = "",
                         example:String          = "",
                         tag:String              = "",
                         cat:String              = ""
                      ) {

  override def toString(): String =
  {
    val text = new StringBuilder()

    text.append( "( name"         +  " : " + name      )
    text.append( ", desc"       +  " : " + desc        )
    text.append( ", dataType"   +  " : " + dataType    )
    text.append( ", storedName" +  " : " + storedName  )
    text.append( ", pos"        +  " : " + pos         )
    text.append( ", isRequired" +  " : " + isRequired  )
    text.append( ", minLength"  +  " : " + minLength   )
    text.append( ", maxLength"  +  " : " + maxLength   )
    text.append( ", defaultVal" +  " : " + defaultVal  )
    text.append( ", example"    +  " : " + example     )
    text.append( ", key"        +  " : " + key         )
    text.append( ", extra"      +  " : " + extra       )
    text.append( ", tag"        +  " : " + tag         )
    text.append( ", cat"        +  " : " + cat         )
    text.append( " )"                                  )
    text.toString()
  }


  def toJSDef(): String =
  {
    val min = minLength
    val max = maxLength
    "\t\t\t{ name:\"" + name + "\", type:\"" + dataTypeJs + "\" , required: " +
      isRequired.toString.toLowerCase() +  ", min: " + min + ", max: " + max + ", cat: \"" + cat + "\" }"
  }


  def toJSInstance(): String =
  {
    val tabs = "\t\t\t"
    if( dataType == Reflector.getFieldTypeString() ) tabs + name + " : " + "\"\""
    else if(dataType == typeOf[Boolean])             tabs + name + " : false"
    else if(dataType == typeOf[Short])               tabs + name + " : 0"
    else if(dataType == typeOf[Int])                 tabs + name + " : 0"
    else if(dataType == typeOf[Long])                tabs + name + " : 0"
    else if(dataType == typeOf[Double])              tabs + name + " : 0"
    else if(dataType == typeOf[DateTime])            tabs + name + " : \"\""
    else                                             tabs + name + " : null"
  }


  def dataTypeSimple():String = {
    if( dataType == Reflector.getFieldTypeString() ) "text:" + maxLength
    else if(dataType == typeOf[Boolean])             "bool"
    else if(dataType == typeOf[Short])               "short"
    else if(dataType == typeOf[Int])                 "int"
    else if(dataType == typeOf[Long])                "long"
    else if(dataType == typeOf[Double])              "double"
    else if(dataType == typeOf[DateTime])            "datetime"
    else                                             "object"
  }


  def isStandard():Boolean = {
    Strings.isMatch(tag, "standard") || ( cat == "id" || cat == "meta" )
  }


  private def dataTypeJs = dataTypeSimple()

}


object ModelField {



  /**
   * builds a new model field that is an id
   * @param name
   * @param dataType
   * @param autoIncrement
   * @return
   */
  def id ( name:String, dataType:Type, autoIncrement:Boolean = false ) : ModelField =
  {
    build(name, typeOf[Long], "", true, 0, 0, Some(name), Some(0), cat = "id")
  }


  /**
   * builds an model field using all the fields supplied.
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
  def build(
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
    field
  }
}