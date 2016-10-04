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

package slate.core.shell


import slate.common.serialization.{SerializerProps, SerializerJson}
import slate.entities.core.{EntitySerializer, Entities, IEntity}
import slate.common._
import slate.common.results.{ResultConverter, ResultTimed}

import scala.collection.immutable.LinearSeq


object ShellPrinter {

  private var _entities:Option[Entities] = None
  private val _writer = new ConsoleWriter()


  def setEntities(entities:Entities): Unit =
  {
    _entities = Some(entities)
  }


  def printResult(result:Result[Any]):Unit = {

    if (result.isEmpty) {
      printEmpty()
      return
    }

    val data = result.get
    if(data != null) {
      printAny(data)
    }
    printSummary(result)
  }


  /**
   * prints empty result
   */
  def printEmpty(): Unit =
  {
    _writer.important("no results/data")
    writeLine()
  }


  /**
   * prints summary of the result.
    *
    * @param result
   */
  def printSummary(result:Result[Any]):Unit = {

    // Stats.
    writeText("Success : " + result.success)
    writeText("Status  : " + result.code)
    writeText("Message : " + result.msg)
    writeText("Tag     : " + result.tag)
  }


  /**
   * prints an item ( non-recursive )
    *
    * @param obj
   */
  def printAny(obj:Any): Unit =
  {
    obj match {
      case null             => writeText( "null" )
      case None             => writeText( "none" )
      case s:Option[Any]    => printAny(s.getOrElse(None))
      case s:String         => writeText( Strings.stringRepresentation(s) )
      case s:Int            => writeText( s.toString )
      case s:Long           => writeText( s.toString )
      case s:Double         => writeText( s.toString )
      case s:Boolean        => writeText( s.toString.toLowerCase )
      case s:DateTime       => writeText( "\"" + s.toString() + "\"" )
      case s:IEntity        => printEntity(s, _entities)
      case s:LinearSeq[Any] => printList(s)
      case s: AnyRef        => { val ser = new SerializerProps(); writeText(ser.serialize(s)); }
      case _                => { writeText(obj.toString); writeLine(); }
    }
  }


  /**
    * prints a list ( recursive
    *
    * @param items
    */
  def printList(items:scala.collection.immutable.LinearSeq[Any]): Unit =
  {
    for(item <- items)
    {
      printAny(item)
    }
  }


  /**
   * prints an entity
    *
    * @param entity
   * @param entities
   */
  def printEntity(entity:IEntity, entities:Option[Entities])
  {
    if(entities.isEmpty)
    {
      return
    }
    // Entity ? Print it as text.
    val mapper = entities.get.getMapper(Reflector.getTypeFromInstance(entity))
    val serializer = new EntitySerializer()
    val text = serializer.serializeToProps(entity, mapper)
    writeText(text)
    writeLine()
  }


  def writeText(text:String): Unit ={
    _writer.text(text)
  }


  def writeLine(): Unit ={
    _writer.line()
  }
}
