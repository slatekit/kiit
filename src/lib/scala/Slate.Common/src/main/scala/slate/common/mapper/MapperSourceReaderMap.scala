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
package slate.common.mapper

import slate.common.{ListMap, DateTime}

class MapperSourceReaderMap(rs:ListMap[String,Any]) extends MappedSourceReader {

  def init(rec:List[String]) : Unit = {
  }


  override def getString(pos: Int): String          = rs.getAt(pos).asInstanceOf[String]
  override def getString(name:String): String       = rs.get(name).asInstanceOf [String]


  override def getShort(pos: Int): Short      = rs.getAt(pos).asInstanceOf[Short]
  override def getShort(name:String): Short   = rs.get(name).asInstanceOf [Short]


  override def getInt(pos: Int): Int          = rs.getAt(pos).asInstanceOf[Int]
  override def getInt(name:String): Int       = rs.get(name).asInstanceOf [Int]


  override def getFloat(pos: Int): Float      = rs.getAt(pos).asInstanceOf[Float]
  override def getFloat(name:String): Float   = rs.get(name).asInstanceOf [Float]


  override def getDouble(pos: Int): Double    = rs.getAt(pos).asInstanceOf[Double]
  override def getDouble(name:String): Double = rs.get(name).asInstanceOf [Double]


  override def getLong(pos: Int): Long        = rs.getAt(pos).asInstanceOf[Long]
  override def getLong(name:String): Long     = rs.get(name).asInstanceOf [Long]


  override def getBool(pos: Int): Boolean     = rs.getAt(pos).asInstanceOf[Boolean]
  override def getBool(name:String): Boolean  = rs.get(name).asInstanceOf [Boolean]


  override def getVersion(): String = ""


  override def getDate(pos: Int): DateTime    = DateTime( rs.getAt(pos).asInstanceOf[java.sql.Timestamp])
  override def getDate(name:String): DateTime = DateTime( rs.get(name).asInstanceOf [java.sql.Timestamp])


  override def getOrDefault(pos: Int, defaultVal: String): String     = rs.getAt(pos).asInstanceOf[String]
  override def getOrDefault(name: String, defaultVal: String): String = rs.get(name).asInstanceOf [String]


  override def getBoolOrDefault(pos: Int, defaultVal: Boolean): Boolean    = rs.getAt(pos).asInstanceOf[Boolean]
  override def getBoolOrDefault(name:String, defaultVal: Boolean): Boolean = rs.get(name).asInstanceOf [Boolean]
}
