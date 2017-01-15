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
    _buffer.append("// " + text + Strings.newline())
  }


  def comments(text:String):Unit = {
    _buffer.append("/* " + text + Strings.newline() + " */" + Strings.newline())
  }


  def header(name:String):Unit = {
    _buffer.append(s"[$name]" + Strings.newline())
    _indenter.inc()
  }


  def keyValue(name:String, value:String):Unit = {
    _buffer.append(s"$name: " + value + Strings.newline())
  }


  def fieldDef(field:ModelField):Unit = {
    _buffer.append(field.name + ",")
    _buffer.append(field.dataTypeSimple() + ","        )
    _buffer.append(field.isRequired + ","              )
    _buffer.append(field.defaultVal.getOrElse("") + ",")
    _buffer.append(field.storedName + ","              )
    _buffer.append(field.key + ","                     )
    _buffer.append(field.extra + Strings.newline()     )
  }


  def actionDef(name:String, value:String):Unit = {
    _buffer.append(s"$name: " + value + Strings.newline())
  }


  def newline(name:String):Unit = {
    _buffer.append(Strings.newline())
  }


  override def end():Unit = {
    _indenter.dec()
  }
}
