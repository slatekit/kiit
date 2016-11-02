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

package slate.entities.models

import slate.common.serialization.ObjectBuilder
import slate.common.{Model, ModelField, Strings}

class ModelSerializer extends ObjectBuilder(true, "  ") {


  def serialize(model:Model):String = {
    comment("Model definition file for : " + model.name)
    header("MODEL")
      keyValue("name" , model.name)
      keyValue("desc" , model.desc)
      keyValue("table", model.table)
      keyValue("class", model.fullName)
    end()
    newLine()
    
    comment("All fields supported: name, type, required, default, column-name, key, extra")
    header("FIELDS")
    for(field <- model.fields){
      fieldDef( field )
    }
    newLine()

    comment("All actions available on model")
    header("ACTIONS")
    newLine()
    toString()
  }


  def comment(text:String):Unit = {
    _buffer = _buffer + "// " + text + Strings.newline()
  }


  def comments(text:String):Unit = {
    _buffer = _buffer + "/* " + text + Strings.newline() + " */" + Strings.newline()
  }


  def header(name:String):Unit = {
    _buffer = _buffer + s"[$name]" + Strings.newline()
    indentInc()
  }


  def keyValue(name:String, value:String):Unit = {
    _buffer = _buffer + s"$name: " + value + Strings.newline()
  }


  def fieldDef(field:ModelField):Unit = {
    _buffer = _buffer + field.name + ","
    _buffer = _buffer + field.dataTypeSimple() + ","
    _buffer = _buffer + field.isRequired + ","
    _buffer = _buffer + field.defaultVal.getOrElse("") + ","
    _buffer = _buffer + field.storedName + ","
    _buffer = _buffer + field.key + ","
    _buffer = _buffer + field.extra + Strings.newline()
  }


  def actionDef(name:String, value:String):Unit = {
    _buffer = _buffer + s"$name: " + value + Strings.newline()
  }


  def newline(name:String):Unit = {
    _buffer = Strings.newline()
  }


  override def end():Unit = {
    indentDec()
  }
}
