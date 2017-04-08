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
import slate.common.reflect.{ReflectedArg, ReflectedClass}
import slate.common.results.{ResultSupportIn}
import slate.common.utils.Temp
import slate.core.apis.Request
import slate.core.apis.core.Action

import scala.reflect.runtime.universe.typeOf
import slate.common._

import scala.reflect.runtime.universe.Type
import scala.collection.mutable.ListBuffer

/**
  * Created by kreddy on 3/15/2016.
  */
object ApiCallHelper extends ResultSupportIn {

  protected val TypeDecString = typeOf[DecString]
  protected val TypeDecInt    = typeOf[DecInt]
  protected val TypeDecLong   = typeOf[DecLong]
  protected val TypeDecDouble = typeOf[DecDouble]
  protected val TypeDoc       = typeOf[Doc]
  protected val TypeVars      = typeOf[Vars]
  protected val TypeRequest   = typeOf[Request]


  /**
   * Builds a string parameter ensuring that nulls are avoided.
   * @param args
   * @param paramName
   * @return
   */
  def handleStringParam(args:Inputs, paramName:String): String = {
    val text = args.getString(paramName)
    val isNull = "null".equalsIgnoreCase(text)

    // As a design choice, this marshaller will only pass empty string to
    // API methods instead of null
    if (isNull || text == null )
      ""
    else
      text
  }


  /**
   * Builds a Doc object by reading the file content from the referenced uri
   * e.g.
   * 1. "user://slatekit/temp/file1.txt"    reference user directory
   * 2. "file://c:/slatekit/temp/file.txt"  reference file explicitly
   * @param args
   * @param paramName
   * @return
   */
  def handleDocParam(args:Inputs, paramName:String): Doc = {
    val uri = args.getString(paramName)
    val doc = Uris.readDoc(uri)
    doc.getOrElse(new Doc("", "", "", 0))
  }


  /**
   * Builds a Vars object which is essentially a lookup of items by both index and key
   * TODO: Add support for construction for various types, e..g string, list, map.
   * @param args
   * @param paramName
   * @return
   */
  def handleVarsParam(args:Inputs, paramName:String): Vars = {
    val text = args.getString(paramName)
    Vars(text)
  }


  /**
   * Handles building of a list from various source types
   * @param args
   * @param paramName
   * @return
   */
  def handleList(parameter:ReflectedArg, args:Inputs,  paramName:String): List[Any] = {
    val listType = parameter.tpe.typeArgs.head
    val items = args.getList(paramName, listType)
    items
  }


  /**
   * Handle building of a map from various sources
   * @param parameter
   * @param args
   * @param paramName
   * @return
   */
  def handleMap(parameter:ReflectedArg, args:Inputs, paramName:String): Map[_,_] = {
    val keyType = parameter.tpe.typeArgs.head
    val valType = parameter.tpe.typeArgs(1)
    val items = args.getMap(paramName, keyType, valType)
    items
  }


  /**
   * Handle building of an object from various sources.
   * @param parameter
   * @param args
   * @param paramName
   * @return
   */
  def handleObject(parameter:ReflectedArg, args:Inputs, paramName:String): AnyRef = {
    val complexObj = args.getObject(paramName)
    complexObj.fold[Any](null) {
        case "null"  => None
        case "\"\""  => None
        case obj       => buildArgInstance(parameter, obj.asInstanceOf[Inputs])
      }
    complexObj
  }


  /**
   * Builds a complex object
   * @param args
   * @param parameter
   * @param paramName
   * @return
   */
  def handleComplex(args:Inputs, parameter:ReflectedArg, paramName:String): AnyRef = {
    val fullName = parameter.tpe.typeSymbol.asClass.fullName
    if(fullName == "scala.collection.immutable.List") {
      handleList(parameter, args, paramName)
    }
    else if(fullName == "scala.collection.immutable.Map") {
      handleMap(parameter, args, paramName)
    }
    else {
      handleObject(parameter, args, paramName)
    }
  }


  // Improve: Check this article:
  // http://www.cakesolutions.net/teamblogs/ways-to-pattern-match-generic-types-in-scala
  def fillArgsExact(callReflect:Action, cmd:Request, args:Inputs, allowLocalIO:Boolean = false,
                    enc:Option[Encryptor] = None): Array[Any] =
  {
    // Check each parameter to api call
    val inputs = new ListBuffer[Any]()
    for(ndx <- callReflect.paramList.indices )
    {
      // Get each parameter to the method
      val parameter = callReflect.paramList(ndx)
      val paramName = parameter.name
      val paramType = parameter.tpe
      val result = paramType match {

        // Basic types
        case BoolType       => args.getString(paramName).toBoolean
        case ShortType      => args.getString(paramName).toShort
        case IntType        => args.getString(paramName).toInt
        case LongType       => args.getString(paramName).toLong
        case FloatType      => args.getString(paramName).toFloat
        case DoubleType     => args.getString(paramName).toDouble
        case StringType     => handleStringParam(args, paramName)
        case DateType       => DateTime.parseNumericVal(args.getString(paramName))

        // Raw request
        case TypeRequest    => cmd

        // Doc/File reference ( only if allowed )
        case TypeDoc        => handleDocParam(args, paramName)

        // Map from string string delimited pairs
        case TypeVars       => handleVarsParam(args, paramName)

        // Decryption from encrypted types
        case TypeDecInt     => enc.fold(new DecInt(0))    ( e => new DecInt(e.decrypt(args.getString(paramName)).toInt      ))
        case TypeDecLong    => enc.fold(new DecLong(0L))  ( e => new DecLong(e.decrypt(args.getString(paramName)).toLong    ))
        case TypeDecDouble  => enc.fold(new DecDouble(0D))( e => new DecDouble(e.decrypt(args.getString(paramName)).toDouble))
        case TypeDecString  => enc.fold(new DecString(""))( e => new DecString(e.decrypt(args.getString(paramName))         ))

        // Complex type
        case _              => handleComplex(args, parameter, paramName)
      }

      inputs.append(result)
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
