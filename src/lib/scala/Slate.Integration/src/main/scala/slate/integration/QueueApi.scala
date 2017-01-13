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
package slate.integration

import java.nio.file.Paths

import slate.common.{Result, Doc, Files}
import slate.common.queues.{QueueSourceMsg, QueueSource}
import slate.core.apis.{Api, ApiAction}
import slate.core.common.svcs.ApiWithSupport

import scala.collection.mutable.ListBuffer


@Api(area = "infra", name = "queues", desc = "api info about the application and host", roles= "admin", auth="key-roles", verb = "post", protocol = "*")
class QueueApi ( val queue:QueueSource) extends ApiWithSupport {


  @ApiAction(name = "", desc= "connect to the queue", roles= "@parent", verb = "@parent", protocol = "@parent")
  def connectWith(key:String, password:String, tag:String):Unit =
  {
    queue.connectWith(key, password, tag)
  }


  @ApiAction(name = "", desc= "close the queue", roles= "@parent", verb = "@parent", protocol = "@parent")
  def close(): Unit =
  {
    queue.close()
  }


  @ApiAction(name = "", desc= "get the total items in the queue", roles= "@parent", verb = "@parent", protocol = "@parent")
  def count(): Int =
  {
    queue.count()
  }


  @ApiAction(name = "", desc= "get the next item in the queue", roles= "@parent", verb = "@parent", protocol = "@parent")
  def next(complete:Boolean):Option[Any] =
  {
    val item = queue.next()
    if(complete){
      queue.complete(item)
    }
    item
  }


  @ApiAction(name = "", desc= "get the next set of items in the queue", roles= "@parent", verb = "@parent", protocol = "@parent")
  def nextBatch(size:Int = 10, complete:Boolean):Option[List[Any]] =
  {
    val items = queue.nextBatch(size)
    if(complete) {
      items.map( items => {
        for(item <- items){
          queue.complete(Option(item))
        }
        items
      })
    }
    items
  }


  @ApiAction(name = "", desc= "gets next item and saves it to file", roles= "@parent", verb = "@parent", protocol = "@parent")
  def nextToFile(complete:Boolean, fileNameLocal:String):Option[Any] =
  {
    val item = queue.next()
    if(complete){
      queue.complete(item)
    }
    writeToFile(item, fileNameLocal, 0, getContent)
  }


  @ApiAction(name = "", desc= "gets next set of items and saves them to files", roles= "@parent", verb = "@parent", protocol = "@parent")
  def nextBatchToFiles(size:Int = 10, complete:Boolean, fileNameLocal:String):Option[List[Option[String]]] =
  {
    val items = queue.nextBatch(size)
    val messages = ListBuffer[Option[String]]()
    items.fold(Option(List[Option[String]](Some("No items available"))))( all => {
      for(ndx <- 0 until all.size){
        val item = all(ndx)
        messages.append( writeToFile(Option(item), fileNameLocal, ndx, getContent) )
      }
      Option(messages.toList)
    })
  }


  @ApiAction(name = "", desc= "sends a message to the queue", roles= "@parent", verb = "@parent", protocol = "@parent")
  def send(msg: String, tagName:String = "", tagValue:String = "") : Result[String] =
  {
    queue.send(msg, tagName, tagValue)
  }


  @ApiAction(name = "", desc= "sends a message to queue using content from file", roles= "@parent", verb = "@parent", protocol = "@parent")
  def sendFromFile(uri:String, tagName:String = "", tagValue:String = "") : Result[String] =
  {
    queue.sendFromFile(uri, tagName, tagValue)
  }


  @ApiAction(name = "", desc= "sends a message to queue using content from file", roles= "@parent", verb = "@parent", protocol = "@parent")
  def sendFromDoc(doc:Doc, tagName:String = "", tagValue:String = "") : Result[String] =
  {
    queue.send(doc.content, tagName, tagValue)
  }


  private def getContent(msg:Option[Any]):String = {
    queue.asInstanceOf[QueueSourceMsg].getMessageBody(msg)
  }
}
