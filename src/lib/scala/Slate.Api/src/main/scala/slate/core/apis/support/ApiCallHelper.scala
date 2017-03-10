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

package slate.core.apis.support

import slate.common.encrypt._
import slate.common.reflect.ReflectConsts._
import slate.common.reflect.{ReflectedClass, ReflectedArg}
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
  protected val _typeDefaults = Map[String,Any](
   "String"  -> "",
   "Boolean" -> false,
   "Int"     -> 0,
   "Long"    -> 0L,
   "Double"  -> 0d,
   "DateTime"-> DateTime.now()
  )


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
               enc:Option[Encryptor] = None): Array[Any] = {
    // Check 1: No args ?
    if (!callReflect.hasArgs)
      Array[Any]()
    // Check 2: 1 param with default and no args
    else if (callReflect.isSingleDefaultedArg() && args.size() == 0) {
      val argType = callReflect.paramList(0).typeName
      val defaultVal = if(_typeDefaults.contains(argType))_typeDefaults(argType) else None
      Array[Any](defaultVal)
    }
    else {
      fillArgsExact(callReflect, cmd, args, allowLocalIO, enc)
    }
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
        val isNull = "null".equalsIgnoreCase(text)

        // As a design choice, this marshaller will only pass empty string to
        // API methods instead of null
        if(isNull) "" else text
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
        val complexObj = args.getObject(paramName)
        complexObj.fold[Any](null) {
            case "null"  => None
            case "none"  => None
            case "\"\""  => None
            case obj       => buildArgInstance(parameter, obj.asInstanceOf[Inputs])
          }
      }

      inputs.append(paramValue)
    }
    inputs.toArray[Any]
  }


  private def buildArgInstance(parameter:ReflectedArg, inputs:Inputs):Any = {
    // Create object
    val isCaseClass = Reflector.isCaseClass(parameter.asType())
    val reflector = new ReflectedClass(parameter.asType())
    val instance = if(isCaseClass){
      reflector.createWithDefaults(inputs)
    }
    else {
      reflector.updateVars(inputs)
    }
    instance
  }
}
