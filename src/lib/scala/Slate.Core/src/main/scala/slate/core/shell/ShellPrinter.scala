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

package slate.core.shell


import slate.common.console.ConsoleWriter
import slate.common.serialization.{SerializerProps, SerializerJson}
import slate.entities.core.{EntitySerializer, Entities, IEntity}
import slate.common._

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
    }
    else {
      val data = result.get
      if (data != null) {
        printAny(data)
      }
      printSummary(result)
    }
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
      case s:Unit           => println("no result")
      case None             => writeText( "none" )
      case s:Option[Any]    => printAny(s.getOrElse(None))
      case s:Result[Any]    => printAny(s.getOrElse(None))
      case s:String         => writeText( Strings.toStringRep(s) )
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
  def printEntity(entity:IEntity, entities:Option[Entities]):Unit =
  {
    if(entities.isDefined) {
      // Entity ? Print it as text.
      val mapper = entities.get.getMapper(Reflector.getTypeFromInstance(entity))
      val serializer = new EntitySerializer()
      val text = serializer.serializeToProps(entity, mapper)
      writeText(text)
      writeLine()
    }
  }


  def writeText(text:String): Unit ={
    _writer.text(text)
  }


  def writeLine(): Unit ={
    _writer.line()
  }
}
