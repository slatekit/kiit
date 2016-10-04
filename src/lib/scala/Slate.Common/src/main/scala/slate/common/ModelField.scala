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
    var text = ""

    text += "( name"         +  " : " + name
    text += ", desc"       +  " : " + desc
    text += ", dataType"   +  " : " + dataType
    text += ", storedName" +  " : " + storedName
    text += ", pos"        +  " : " + pos
    text += ", isRequired" +  " : " + isRequired
    text += ", minLength"  +  " : " + minLength
    text += ", maxLength"  +  " : " + maxLength
    text += ", defaultVal" +  " : " + defaultVal
    text += ", example"    +  " : " + example
    text += ", key"        +  " : " + key
    text += ", extra"      +  " : " + extra
    text += ", tag"        +  " : " + tag
    text += ", cat"        +  " : " + cat
    text += " )"
    text
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
    if( dataType == Reflector.getFieldTypeString() )
    {
      tabs + name + " : " + "\"\""
    }
    else if(dataType == typeOf[Boolean])
    {
      tabs + name + " : false"
    }
    else if(dataType == typeOf[Short])
    {
      tabs + name + " : 0"
    }
    else if(dataType == typeOf[Int])
    {
      tabs + name + " : 0"
    }
    else if(dataType == typeOf[Long])
    {
      tabs + name + " : 0"
    }
    else if(dataType == typeOf[Double])
    {
      tabs + name + " : 0"
    }
    else if(dataType == typeOf[DateTime])
    {
      tabs + name + " : \"\""
    }
    else // Object
    {
      tabs + name + " : null"
    }
  }


  def dataTypeSimple():String = {
    if( dataType == Reflector.getFieldTypeString() )
    {
      "text:" + maxLength
    }
    else if(dataType == typeOf[Boolean])
    {
      "bool"
    }
    else if(dataType == typeOf[Short])
    {
      "short"
    }
    else if(dataType == typeOf[Int])
    {
      "int"
    }
    else if(dataType == typeOf[Long])
    {
      "long"
    }
    else if(dataType == typeOf[Double])
    {
      "double"
    }
    else if(dataType == typeOf[DateTime])
    {
      "datetime"
    }
    else // Object
    {
      "object"
    }
  }


  def isStandard():Boolean = {
    Strings.isMatch(tag, "standard") || ( cat == "id" || cat == "meta" )
  }


  private def dataTypeJs = dataTypeSimple()
}