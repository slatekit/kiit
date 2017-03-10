/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.queues

import slate.common._
import slate.common.results.ResultFuncs._

import scala.collection.mutable.ListBuffer


/**
  * Used in Unit-Tests, for internal-use only.
  * Refer to the AWS SQS Cloud Queue for an actual implementation.
  *
  * NOTE: This should not be used in production environment.
  */
class QueueSourceDefault extends QueueSource with QueueSourceMsg {

  protected val _list = new ListBuffer[Any]()
  private val _object = new Object()


  def init():Unit = {
  }


  override def count(): Int =
  {
    synchronized
    {
      _list.size
    }
  }


  override def next():Option[Any] =
  {
    synchronized
    {
      if(_list.isEmpty) None else Some(_list.remove(0))
    }
  }


  override def nextBatchAs[T](size:Int = 10):Option[List[T]] = {
    nextBatch(size).map[List[T]]( all => {
      val items = all.map( item => item.asInstanceOf[T] )
      items.toList
    })
  }


  override def nextBatch(size:Int = 10):Option[List[Any]] =
  {
    synchronized
    {
      if(_list.isEmpty) None
      else
      {
        val results = new ListBuffer[Any]()
        val actualSize = Math.min(size, _list.size)
        for (ndx <- 0 until actualSize)
        {
          val msg = _list.remove(0)
          results += msg
        }
        Some(results.toList)
      }
    }
  }

  override def send(msg: Any, tagName:String = "", tagValue:String = "") : Result[String] =
  {
    synchronized
    {
      val id = Random.stringGuid()
      _list += new QueueSourceData(msg, Some(Map( tagName -> tagValue) ), Some(id) )
      success(id)
    }
  }


  override def send(msg:String, attributes:Map[String,Any]) : Result[String] =
  {
    synchronized
    {
      val id = Random.stringGuid()
      _list +=  new QueueSourceData(msg, Some(attributes), Some(id) )
      success(id)
    }
  }


  override def sendFromFile(fileNameLocal:String, tagName:String = "", tagValue:String = "") : Result[String] =
  {
    val path = Uris.interpret(fileNameLocal)
    path.fold[Result[String]](failure(msg = Some("Invalid file path: " + fileNameLocal)))( pathLocal => {
      val content = Files.readAllText(pathLocal)
      send(content, tagName, tagValue)
    })
  }


  override def complete(item:Option[Any]): Unit =
  {
    if(!item.isEmpty) {
      discard(item.get)
    }
  }


  override def completeAll(items:Option[List[Any]]):Unit =
  {
    synchronized
    {
      if(!items.isEmpty) {
        val all = items.get
        for (item <- all) {
          val data = item.asInstanceOf[QueueSourceData]
          val pos = _list.indexOf(data)
          _list.remove(pos)
        }
      }
    }
  }


  override def abandon(item:Option[Any]): Unit =
  {
    if(item.isDefined) {
      discard(item.get)
    }
  }


  override def toString(item:Option[Any]):String =
  {
    item.getOrElse("").toString()
  }


  override def getMessageBody(msgItem:Option[Any]):String =
  {
    getMessageItemProperty(msgItem, (data) => data.message.toString)
  }


  override def getMessageTag(msgItem:Option[Any], tagName:String):String =
  {
    getMessageItemProperty(msgItem, (data) => {
      if(data.tags.isEmpty)
        ""
      else
        data.tags.get(tagName).toString
    })
  }


  private def getMessageItemProperty(msgItem:Option[Any], callback:(QueueSourceData) => String)
    :String =
  {
    if(msgItem.isEmpty){
      ""
    }
    else {
     val item = msgItem.get
     if(item.isInstanceOf[QueueSourceData]){
      callback(item.asInstanceOf[QueueSourceData])
     }
     else
       ""
    }
  }


  private def discard(item:Any):Unit =
  {
    synchronized
    {
      val data = item.asInstanceOf[QueueSourceData]
      val pos = _list.indexOf(data)
      //if (pos == -1 ){
      //  pos = _list.indexWhere( (i) => {
      //    val id = i.asInstanceOf[QueueSourceData].id.get
      //    val destId = data.id.get
      //    val isMatch = Strings.isMatch(id, destId)
      //    isMatch
      //  })
      //}
      if (pos > -1) {
        _list.remove(pos)
      }
    }
  }
}
