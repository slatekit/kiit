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

package slate.common.queues



abstract class QueueSource {

  def connect(args:Any):Unit =
  {

  }


  def close(): Unit =
  {

  }


  def count(): Int =
  {
    0
  }


  def next():Option[Any] =
  {
    None
  }


  def nextBatch(size:Int = 10):Option[List[Any]] =
  {
    None
  }


  def complete(item:Option[Any]): Unit =
  {

  }


  def completeAll(items:Option[List[Any]]):Unit =
  {

  }


  def abandon(item:Option[Any]): Unit =
  {

  }


  def toString(item:Option[Any]):String =
  {
    ""
  }


  def send(msg: Any, tagName:String = "", tagValue:String = "") : Unit =
  {

  }


  def send(message:String, attributes:Map[String,Any]) : Unit =
  {

  }
}
