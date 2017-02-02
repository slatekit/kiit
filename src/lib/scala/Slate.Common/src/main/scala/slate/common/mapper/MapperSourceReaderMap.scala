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


  override def get(pos: Int): String          = rs.getAt(pos).get.asInstanceOf[String]
  override def get(name:String): String       = rs.get(name).get.asInstanceOf [String]


  override def getShort(pos: Int): Short      = rs.getAt(pos).get.asInstanceOf[Short]
  override def getShort(name:String): Short   = rs.get(name).get.asInstanceOf [Short]


  override def getInt(pos: Int): Int          = rs.getAt(pos).get.asInstanceOf[Int]
  override def getInt(name:String): Int       = rs.get(name).get.asInstanceOf [Int]


  override def getDouble(pos: Int): Double    = rs.getAt(pos).get.asInstanceOf[Double]
  override def getDouble(name:String): Double = rs.get(name).get.asInstanceOf [Double]


  override def getLong(pos: Int): Long        = rs.getAt(pos).get.asInstanceOf[Long]
  override def getLong(name:String): Long     = rs.get(name).get.asInstanceOf [Long]


  override def getBool(pos: Int): Boolean     = rs.getAt(pos).get.asInstanceOf[Boolean]
  override def getBool(name:String): Boolean  = rs.get(name).get.asInstanceOf [Boolean]


  override def getVersion(): String = ""


  override def getDate(pos: Int): DateTime    = new DateTime( rs.getAt(pos).get.asInstanceOf[java.sql.Timestamp])
  override def getDate(name:String): DateTime = new DateTime( rs.get(name).get.asInstanceOf [java.sql.Timestamp])


  override def getOrDefault(pos: Int, defaultVal: String): String     = rs.getAt(pos).get.asInstanceOf[String]
  override def getOrDefault(name: String, defaultVal: String): String = rs.get(name).get.asInstanceOf [String]


  override def getBoolOrDefault(pos: Int, defaultVal: Boolean): Boolean    = rs.getAt(pos).get.asInstanceOf[Boolean]
  override def getBoolOrDefault(name:String, defaultVal: Boolean): Boolean = rs.get(name).get.asInstanceOf [Boolean]
}
