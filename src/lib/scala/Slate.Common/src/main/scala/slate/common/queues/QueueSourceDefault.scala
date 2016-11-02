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

import slate.common.{Random, Strings}

import scala.collection.mutable.ListBuffer


class QueueSourceDefault extends QueueSource with QueueSourceMsg {

  protected val _list = new ListBuffer[Any]()
  private val _object = new Object()


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

  override def send(msg: Any, tagName:String = "", tagValue:String = "") : Unit =
  {
    synchronized
    {
      val id = Random.stringGuid()
      _list += new QueueSourceData(msg, Some(Map( tagName -> tagValue) ), Some(id) )
    }
  }


  override def send(msg:String, attributes:Map[String,Any]) : Unit =
  {
    synchronized
    {
      _list +=  new QueueSourceData(msg, Some(attributes) )
    }
  }


  override def complete(item:Option[Any]): Unit =
  {
    if(item.isEmpty)return
    discard(item.get)
  }


  override def completeAll(items:Option[List[Any]]):Unit =
  {
    synchronized
    {
      if(items.isEmpty)return
      val all = items.get
      for(item <- all)
      {
        val data = item.asInstanceOf[QueueSourceData]
        val pos = _list.indexOf(data)
        _list.remove(pos)
      }
    }
  }


  override def abandon(item:Option[Any]): Unit =
  {
    if(item.isEmpty)return
    discard(item.get)
  }


  override def toString(item:Option[Any]):String =
  {
    if(item.isEmpty) return Strings.empty
    item.get.toString
  }


  override def getMessageBody(msgItem:Option[Any]):String =
  {
    if(msgItem.isEmpty) return ""
    val item = msgItem.get
    if(!item.isInstanceOf[QueueSourceData]) return ""
    val data = item.asInstanceOf[QueueSourceData]
    data.message.toString
  }


  override def getMessageTag(msgItem:Option[Any], tagName:String):String =
  {
    if(msgItem.isEmpty) return ""
    val item = msgItem.get
    if(!item.isInstanceOf[QueueSourceData]) return ""
    val data = item.asInstanceOf[QueueSourceData]
    if(data.tags.isEmpty) return ""
    data.tags.get(tagName).toString
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
