/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
*/


package slate.cloud.aws

import slate.common._
import slate.common.results.ResultFuncs._
import slate.core.cloud._

import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model._

import scala.collection.mutable.ListBuffer

/**
 *
 * @param queue   : Name of the SQS Queue
 * @param path    : Path to aws conf file, e.g. Some("user://myapp/conf/sqs.conf")
 * @param section : Name of section in conf file for api key. e.g. Some("sqs")
 */
class AwsCloudQueue(queue:String,
                    path:Option[String] = None,
                    section:Option[String] = None) extends CloudQueueBase
  with AwsSupport
{

  private val _queue = queue
  private val _sqs:AmazonSQSClient = AwsFuncs.sqs(path, section)
  private val _queueUrl = _sqs.getQueueUrl(_queue).getQueueUrl
  private val SOURCE  = "aws:sqs"


  /**
    * hook for any initialization
    */
  override def init():Unit = {
  }


  /**Gets the total number of items in the queue
    *
    * @return
   */
  override def count(): Int =
  {
    val count = execute[Int](SOURCE, "count", rethrow = true, data = None, call = () =>
    {
      val request = new GetQueueAttributesRequest(_queueUrl).withAttributeNames("All")
      val atts = _sqs.getQueueAttributes(request).getAttributes

      // get count
      if ( atts.containsKey("ApproximateNumberOfMessages"))
        Integer.parseInt(atts.get("ApproximateNumberOfMessages"))
      else
        0
    })
    count.getOrElse(0)
  }


  /**Gets the next item in the queue
    *
    * @return : An message object from the underlying queue provider
   */
  override def next():Option[Any] =
  {
    val result = nextBatch(1)
    result.fold[Option[Any]](None)( res => Option(res.head))
  }


  /**Gets the next batch of items in the queue
    *
    * @param size : The number of items to get at once
   * @return : A list of message object from the underlying queue provider
   */
  override def nextBatch(size:Int = 10):Option[List[Any]] =
  {
    val results = execute[List[Any]](SOURCE, "nextbatch", data = Some(size), call = () =>
    {
      val req = new ReceiveMessageRequest(_queueUrl).withMaxNumberOfMessages(size)
      val msgs = _sqs.receiveMessage(req).getMessages
      if(Option(msgs).nonEmpty && msgs.size() > 0) {
        val results = new ListBuffer[Any]()
        for (ndx <- 0 to msgs.size() - 1)
        {
          val msg = msgs.get(ndx)
          results += msg
        }
        results.toList
      }
      else
        List[Any]()
    })
    results
  }


  /** Send a message using either a simple string or a map
    * contains the message data and attributes
    *
    * @param msg: String message, or map containing the fields "message", and "atts"
    */
  override def send(msg: Any, tagName:String = "", tagValue:String = "") : Result[String] =
  {
    val msgResult = msg match {
      case t:String => {
        // Send the message, any message that fails will get caught
        // and the onError method is called for that message
        executeResult[String](SOURCE, "send", data = Some(""), call = () =>
        {
          val message = msg.asInstanceOf[String]
          val req = if(!Strings.isNullOrEmpty(tagName))
          {
            val finalTagValue = if(Strings.isNullOrEmpty(tagValue)) "" else tagValue
            val req = new SendMessageRequest(_queueUrl, message)
                          .addMessageAttributesEntry(tagName, new MessageAttributeValue()
                          .withDataType("String").withStringValue(finalTagValue))
            req
          }
          else {
            new SendMessageRequest(_queueUrl, message)
          }
          val result = _sqs.sendMessage(req)
          result.getMessageId
        })
      }
      case t:Map[String,Any] => {
        val map = msg.asInstanceOf[Map[String, Any]]
        val message = getOrDefault(map, "message", "").asInstanceOf[String]
        val atts = getOrDefault(map, "attributes", Map[String, Any]()).asInstanceOf[Map[String, Any]]
        send(message, atts)
      }
      case _ => {
        successOrError(false, Some(""), Some("unknown message type"))
      }
    }
    msgResult
  }


  /**Sends the message with the attributes supplied to the queue
    *
    * @param message : The message to send
    * @param attributes : Additional attributes to put into the message
    */
  override def send(message:String, attributes:Map[String,Any]) : Result[String] =
  {
    // Send the message, any message that fails will get caught
    // and the onError method is called for that message
    executeResult[String](SOURCE, "send", data = Some(message), call = () =>
    {
      val req = new SendMessageRequest(_queueUrl, message)
      val result = _sqs.sendMessage(req)
      result.getMessageId
    })
  }


  override def sendFromFile(fileNameLocal:String, tagName:String = "", tagValue:String = "") : Result[String] =
  {
    val path = Uris.interpret(fileNameLocal)
    path.fold[Result[String]](failure(msg = Some("Invalid file path: " + fileNameLocal)))( pathLocal => {
      val content = Files.readAllText(pathLocal)
      send(content, tagName, tagValue)
    })
  }


  /** Abandons the message supplied    *
    *
    * @param item : The message to abandon/delete
    */
  override def abandon(item:Option[Any]):Unit = {
    item.fold(Unit)( i => {
      discard(i, "abandon")
      Unit
    })
  }


  /** Completes the message by deleting it from the queue
    *
    * @param item : The message to complete
    */
  override def complete(item:Option[Any]):Unit =
  {
    item.fold(Unit)( i => {
      discard( i, "complete")
      Unit
    })
  }


  /** Completes the message by deleting it from the queue
    *
    * @param items : The messages to complete
    */
  override def completeAll(items:Option[List[Any]]):Unit =
  {
    items.fold(Unit)( all => {
      all.foreach( item => discard(item, "complete"))
      Unit
    })
  }


  override def getMessageBody(msgItem:Option[Any]):String =
  {
    getMessageItemProperty(msgItem, (item) => item.getBody )
  }


  def getMessageTag(msgItem:Option[Any], tagName:String):String =
  {
    getMessageItemProperty(msgItem, (sqsMsg) => {
      val atts =  sqsMsg.getAttributes
      if( Option(atts).isEmpty || !atts.containsKey(tagName))
          Strings.empty
      else {
        val tagVal = atts.get(tagName)
        Option(tagVal).fold(Strings.empty)( t => t.toString())
      }
    })
  }


  private def getOrDefault(map:Map[String,Any], key:String, defaultVal:Any): Any =
  {
    Funcs.getOrElse(map, key, defaultVal)
  }


  private def discard(item:Any, action:String):Unit =
  {
    item match {
      case t:Message => {
        execute(SOURCE, action, data = Some(item), call = () =>
        {
          val message = item.asInstanceOf[Message]
          val msgHandle = message.getReceiptHandle

          _sqs.deleteMessage(new DeleteMessageRequest(_queueUrl, msgHandle))
        })
      }
      case _ => {
        // TODO: Provide some callback/notification mechanism
      }
    }
  }


  def getMessageItemProperty(msgItem:Option[Any], callback:(Message) => String) :String =
    msgItem.fold(Strings.empty) {
      case m: Message => callback(m)
      case _          => Strings.empty
    }
}
