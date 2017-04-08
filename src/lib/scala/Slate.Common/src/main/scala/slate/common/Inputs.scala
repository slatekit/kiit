/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.common

import slate.common.reflect.ReflectConsts._

import scala.reflect.runtime.universe.{Type,typeOf}

/**
  * Base class to support retrieving inputs form multiple sources:
  * 1. command line arguments
  * 2. config settings
  * 3. http requests
  * 4. in-memory settings
  *
  * NOTE: This allows for abstracting the input source to accommodate
  * Slate Kit protocol independent APIs
  */
abstract class Inputs {

  // Get values for core data types, must be implemented in derived classes
  def getString   (key: String) : String
  def getDate     (key: String) : DateTime
  def getBool     (key: String) : Boolean
  def getInt      (key: String) : Int
  def getLong     (key: String) : Long
  def getDouble   (key: String) : Double
  def getFloat    (key: String) : Float

  def get(key: String)          : Option[Any]
  def getObject(key: String)    : Option[AnyRef]
  def containsKey(key: String)  : Boolean
  def size():  Int


  // Get values as Option[T]
  def getStringOpt(key: String) : Option[String  ] = getOpt[String]   ( key, getString )
  def getDateOpt  (key: String) : Option[DateTime] = getOpt[DateTime] ( key, getDate   )
  def getBoolOpt  (key: String) : Option[Boolean ] = getOpt[Boolean]  ( key, getBool   )
  def getIntOpt   (key: String) : Option[Int     ] = getOpt[Int]      ( key, getInt    )
  def getLongOpt  (key: String) : Option[Long    ] = getOpt[Long]     ( key, getLong   )
  def getDoubleOpt(key: String) : Option[Double  ] = getOpt[Double]   ( key, getDouble )
  def getFloatOpt (key: String) : Option[Float   ] = getOpt[Float]    ( key, getFloat  )


  // Get value or default
  def getStringOrElse(key: String, default: String  ) : String   = getOrElse[String]   ( key, getString  , default  )
  def getDateOrElse  (key: String, default: DateTime) : DateTime = getOrElse[DateTime] ( key, getDate    , default  )
  def getBoolOrElse  (key: String, default: Boolean ) : Boolean  = getOrElse[Boolean]  ( key, getBool    , default  )
  def getIntOrElse   (key: String, default: Int     ) : Int      = getOrElse[Int]      ( key, getInt     , default  )
  def getLongOrElse  (key: String, default: Long    ) : Long     = getOrElse[Long]     ( key, getLong    , default  )
  def getDoubleOrElse(key: String, default: Double  ) : Double   = getOrElse[Double]   ( key, getDouble  , default  )
  def getFloatOrElse (key: String, default: Float   ) : Float    = getOrElse[Float]    ( key, getFloat   , default  )


  // Get list and maps
  /**
   * gets a list of items of the type supplied.
   * NOTE: derived classes should override this. e.g. HttpInputs in slatekit.server
   * @param key
   * @param tpe
   * @return
   */
  def getList(key:String, tpe:Type):List[Any] = {
    val converter = Converter.converterFor(tpe)
    val input = get(key)
    val result = input.fold[List[Any]](Nil) {
      case "null" => Nil
      case "\"\"" => Nil
      case s: String => Strings.split(s, ',').toList.map(converter)
      case l: List[tpe] => l
      case _ => Nil
    }
    result
  }


  /**
   * gets a map of items of the type supplied.
   * NOTE: derived classes should override this. e.g. HttpInputs in slatekit.server
   * @param key
   * @return
   */
  def getMap(key:String, tpeKey:Type, tpeVal:Type):Map[_,_] = {
    val keyConverter = Converter.converterFor(tpeKey)
    val valConverter = Converter.converterFor(tpeVal)
    val input = get(key)
    val emptyMap = Map.empty[Any,Any]
    val result = input.fold[Map[Any,Any]](emptyMap) {
      case "null" => emptyMap
      case "\"\"" => emptyMap
      case s: String => Strings.splitToMapOfType(s, ',', true, Some('='),
        Some(keyConverter),
        Some(valConverter)).asInstanceOf[Map[Any,Any]]
      case l: Map[tpeKey,tpeVal] => l.asInstanceOf[Map[Any,Any]]
      case _ => emptyMap
    }
    result
  }


  // Helpers
  protected def getOpt[T](key: String,  fetcher:(String) => T ): Option[T] = if(containsKey(key)) Option(fetcher(key)) else None
  protected def getOrElse[T](key: String, fetcher:(String) => T, default: T ): T = if(containsKey(key)) fetcher(key) else default


}