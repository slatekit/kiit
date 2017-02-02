/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.apis.support

import slate.common.encrypt._
import slate.common.results.{ResultSupportIn}
import slate.common.utils.Temp
import slate.core.apis.Request

import scala.reflect.runtime.universe.typeOf
import slate.common._

import scala.reflect.runtime.universe.Type
import scala.collection.mutable.ListBuffer

/**
  * Created by kreddy on 3/15/2016.
  */
object ApiCallHelper extends ResultSupportIn {

  protected val _tpeString:Type = Reflector.getFieldType(typeOf[Temp], "typeString")
  protected val _decryptedTypes = Map[String,Boolean](
    "DecInt"    -> true,
    "DecLong"   -> true,
    "DecDouble" -> true,
    "DecString" -> true
    )


  def validateArgs(callReflect:ApiCallReflect, args:Inputs): Result[Boolean] =
  {
    var error = ": inputs missing or invalid "
    var totalErrors = 0

    // Check each parameter to api call
    for(input <- callReflect.paramList )
    {
      // parameter not supplied ?
      val paramName = input.name
      if(!args.containsKey(paramName))
      {
        val separator = if(totalErrors == 0 ) "( " else ","
        error += separator + paramName
        totalErrors = totalErrors + 1
      }
    }
    // Any errors ?
    if(totalErrors > 0)
    {
      error = error + " )"
      badRequest( msg = Some("bad request: action " + callReflect.name + error))
    }
    else {
      // Ok!
      ok()
    }
  }


  def fillArgs(callReflect:ApiCallReflect, cmd:Request, args:Inputs, allowLocalIO:Boolean = false,
               enc:Option[Encryptor] = None): Array[Any] =
  {
    // Check 1: No args ?
    if(!callReflect.hasArgs)
      Array[Any]()
    // Check 2: 1 param with default and no args
    else if(callReflect.isSingleDefaultedArg() && args.size() == 0)
      Array[Any](null)
    else
      fillArgsExact(callReflect, cmd, args, allowLocalIO, enc)
  }


  def fillArgsExact(callReflect:ApiCallReflect, cmd:Request, args:Inputs, allowLocalIO:Boolean = false,
                    enc:Option[Encryptor] = None): Array[Any] =
  {
    // Check each parameter to api call
    val inputs = new ListBuffer[Any]()
    for(ndx <- callReflect.paramList.indices )
    {
      // Get each parameter to the method
      val parameter = callReflect.paramList(ndx)
      val paramName = parameter.name
      val paramType = parameter.typeName
      val paramValue:Any = if(paramType == "String")
      {
        val text = args.getString(paramName)
        if("null".equalsIgnoreCase(text.asInstanceOf[String]))
        {
          null
        }
        else
          text
      }
      else if(paramType == "Int")
      {
        args.getString(paramName).toInt
      }
      else if(paramType == "Boolean")
      {
        args.getString(paramName).toBoolean
      }
      else if(paramType == "Long")
      {
        args.getString(paramName).toLong
      }
      else if(paramType == "Double")
      {
        args.getString(paramName).toDouble
      }
      else if(paramType == "DateTime")
      {
        DateTime.parseNumericVal(args.getString(paramName))
      }
      else if(paramType == "ApiCmd")
      {
        cmd
      }
      else if(allowLocalIO && paramType == "Doc"){
        val uri = args.getString(paramName)
        val doc = Uris.readDoc(uri)
        doc.getOrElse(new Doc("", "", "", 0))
      }
      else if(paramType == "Vars"){
        val text = args.getString(paramName)
        Vars(text)
      }
      else if(_decryptedTypes.contains(paramType)){
        val text = args.getString(paramName)
        val decrypted:Any  = paramType match {
          case "DecInt"    => enc.fold(new DecInt(0))    ( e => new DecInt(e.decrypt(text).toInt      ))
          case "DecLong"   => enc.fold(new DecLong(0L))  ( e => new DecLong(e.decrypt(text).toLong    ))
          case "DecDouble" => enc.fold(new DecDouble(0D))( e => new DecDouble(e.decrypt(text).toDouble))
          case "DecString" => enc.fold(new DecString(""))( e => new DecString(e.decrypt(text)         ))
          case _           => DecString("")
        }
        decrypted
      }
      else if (!parameter.isBasicType()){
        val subObj = args.getObject(paramName).asInstanceOf[Inputs]
        buildArgInstance(parameter, subObj)
      }

      inputs.append(paramValue)
    }
    inputs.toArray[Any]
  }


  private def buildArgInstance(parameter:ReflectedArg, inputs:Inputs):Any = {

    // Create object
    val instance = Reflector.createInstance(parameter.asType())

    // Get fields
    val fields = Reflector.getFieldsDeclared(instance.asInstanceOf[AnyRef])

    for(field <- fields) {
      val name = field.symbol.name.toString().trim()
      if ( inputs.containsKey(name)){

        val dataType = field.symbol.typeSignature.resultType
        if( dataType == _tpeString )
        {
          val sVal = inputs.getString(name)
          Reflector.setFieldValue(instance, name, sVal)
        }
        else if(dataType == typeOf[Boolean])
        {
          val bVal = inputs.getBool(name)
          Reflector.setFieldValue(instance, name, bVal)
        }
        else if(dataType == typeOf[Int])
        {
          val iVal = inputs.getInt(name)
          Reflector.setFieldValue(instance, name, iVal)
        }
        else if(dataType == typeOf[Long])
        {
          val lVal = inputs.getLong(name)
          Reflector.setFieldValue(instance, name, lVal)
        }
        else if(dataType == typeOf[Double])
        {
          val dVal = inputs.getDouble(name)
          Reflector.setFieldValue(instance, name, dVal)
        }
        else if(dataType == typeOf[DateTime])
        {
          val dVal = inputs.getDate(name)
          Reflector.setFieldValue(instance, name, dVal)
        }
      }
    }
    instance
  }
}
