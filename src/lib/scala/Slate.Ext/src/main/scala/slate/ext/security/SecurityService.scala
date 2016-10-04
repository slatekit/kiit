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
package slate.ext.security

import slate.common.{Files, Strings}
import slate.common.encrypt.Encryptor
import slate.core.common.AppContext
import slate.core.common.tenants.Tenant
import scala.collection.mutable.ListBuffer


class SecurityService() {

  var ctx:AppContext = null


  def encrypt(text:String):String =
  {
    perform( text, enc => enc.encrypt(text) )
  }


  def decrypt(text:String):String =
  {
    perform( text, enc => enc.decrypt(text) )
  }


  def encryptKeyValue(key:String, value:String): String =
  {
    val encrypted =  perform(value, enc => encrypt(value))
    key + ": \"" + encrypted + "\""
  }


  def decryptKeyValue(key:String, value:String): String =
  {
    val plain =  perform( value, enc => enc.decrypt(value))
    key + ": \"" + plain + "\""
  }


  def encryptTokens(name:String, value:String): List[String] =
  {
    if(Strings.isNullOrEmpty(value)){
      return List[String](s"$name : ''")
    }
    val tokens = Strings.split(value, ',')
    val all = ListBuffer[String]()
    var count = 0
    for(token <- tokens){

      val encrypted = perform( token, enc => enc.encrypt(token) )
      all.append(name + count +" : \"" + encrypted + "\"")
      count = count + 1
    }
    all.toList
  }


  def encryptSettingsFile(path:String):String = {
    handleSettingsFile(path, (value) => perform( value, enc => encrypt(value)) )
  }



  def decryptSettingsFile(path:String):String = {
    handleSettingsFile(path, (value) => perform( value, enc => enc.decrypt(value) ) )
  }


  def handleSettingsFile(path:String, converter:(String)=> String):String = {
    val lines = Files.readAllLines(path)
    if(lines.size > 0){
      var buffer = ""
      val newLine = Strings.newline()
      for(line <- lines){
        if(line != newLine && !Strings.isNullOrEmpty(line) && line != "\r\n") {
          val tokens = Strings.split(line, '=')
          val key = tokens(0).trim
          var value = tokens(1).trim
          val containsQuotes = value.contains("\"")
          if (containsQuotes) {
            value = value.replaceAllLiterally("\"", "")
            val converted = converter(value)
            val convertedLine = key + " : \"" + converted + "\"" + newLine
            buffer = buffer + convertedLine
          }
          else {
            val convertedLine = key + " : " + value + newLine
            buffer = buffer + convertedLine
          }
        }
      }
      val path = ctx.dirs.fold("")( dirs => {
        Files.writeFileForDateAsTimeStamp(dirs.pathToOutputs, buffer)
      })
      return "Processed file to location: " + path
    }
    "Settings file did not contain data"
  }


  def perform(text:String, callback: (Encryptor) => String) : String = {
    if(!ctx.enc.isDefined) { return text }
    callback(ctx.enc.get)
  }
}
