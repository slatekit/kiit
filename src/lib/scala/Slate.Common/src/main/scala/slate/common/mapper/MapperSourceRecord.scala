/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.mapper

import java.sql.ResultSet
import slate.common.DateTime


class MapperSourceRecord(val rs:ResultSet) extends MappedSourceReader {


  override def init(rec: List[String]): Unit = ???


  override def get(pos: Int): String = rs.getString(pos)


  override def get(name:String): String = rs.getString(name)


  override def getShort(pos: Int): Short = rs.getShort(pos)


  override def getShort(name:String): Short = rs.getShort(name)


  override def getInt(pos: Int): Int = rs.getInt(pos)


  override def getInt(name:String): Int = rs.getInt(name)


  override def getDouble(pos: Int): Double = rs.getDouble(pos)


  override def getDouble(name:String): Double = rs.getDouble(name)


  override def getLong(pos: Int): Long = rs.getLong(pos)


  override def getLong(name:String): Long = rs.getLong(name)


  override def getBool(pos: Int): Boolean = rs.getBoolean(pos)


  override def getBool(name:String): Boolean = rs.getBoolean(name)


  override def getVersion(): String = ""


  override def getDate(pos: Int): DateTime = DateTime(rs.getTimestamp(pos))


  override def getDate(name:String): DateTime =  DateTime(rs.getTimestamp(name))


  override def getOrDefault(pos: Int, defaultVal: String): String = rs.getString(pos)


  override def getOrDefault(name: String, defaultVal: String): String = rs.getString(name)


  override def getBoolOrDefault(pos: Int, defaultVal: Boolean): Boolean = rs.getBoolean(pos)


  override def getBoolOrDefault(name:String, defaultVal: Boolean): Boolean = rs.getBoolean(name)
}
