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


import slate.common.DateTime


trait MappedSourceReader {
   def init(rec:List[String]) : Unit

   def getVersion() : String

   def get(pos:Int) : String
   def get(name:String) : String

   def getShort(pos:Int) : Int
   def getShort(name:String) : Int

   def getInt(pos:Int) : Int
   def getInt(name:String) : Int

   def getLong(pos:Int) : Long
   def getLong(name:String) : Long

   def getDouble(pos:Int): Double
   def getDouble(name:String): Double

   def getBool(pos:Int) : Boolean
   def getBool(name:String) : Boolean

   def getDate(pos:Int) : DateTime
   def getDate(name:String) : DateTime

   def getOrDefault(pos:Int, defaultVal:String) : String
   def getOrDefault(name:String, defaultVal:String) : String

   def getBoolOrDefault(pos:Int, defaultVal:Boolean) : Boolean
   def getBoolOrDefault(name:String, defaultVal:Boolean) : Boolean
}
