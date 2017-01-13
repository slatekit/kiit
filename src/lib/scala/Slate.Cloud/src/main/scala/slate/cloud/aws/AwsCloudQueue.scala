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


package slate.cloud.aws

import com.amazonaws.auth.AWSCredentials
import slate.common._
import slate.common.results.ResultFuncs._
import slate.core.cloud._

import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model._

import scala.collection.mutable.ListBuffer


class AwsCloudQueue(queue:String) extends CloudQueueBase
  with AwsSupport
{

  private val _queue = queue
  private var _queueUrl = ""
  private var _sqs:AmazonSQSClient = null
  private val SOURCE  = "aws:sqs"


  def this(apiKey:ApiCredentials) = {
    this( apiKey.account)
    connectWith(apiKey.key, apiKey.pass, apiKey.tag)
  }


  override def connectWith(key:String, password:String, tag:String):Unit =
  {
    execute(SOURCE, "connect", rethrow = true, data = None, call = () =>
    {
      val creds = credentials(key, password)
      connect(creds)
    })
  }


  override def connect(args:Any):Unit =
  {
    execute(SOURCE, "connect", rethrow = true, data = Some(args), call = () =>
    {
      val creds = credentialsFromLogon()
      connect(creds)
    })
  }


  /**Gets the total number of items in the queue
   * @return
   */
  override def count(): Int =
  {
    var count = 0
    execute(SOURCE, "count", rethrow = true, data = None, call = () =>
    {
      val request = new GetQueueAttributesRequest(_queueUrl).withAttributeNames("All")
      val atts = _sqs.getQueueAttributes(request).getAttributes

      // get count
      if ( atts.containsKey("ApproximateNumberOfMessages"))
      {
        count = Integer.parseInt(atts.get("ApproximateNumberOfMessages"))
      }
    })
    count
  }


  /**Gets the next item in the queue
   * @return : An message object from the underlying queue provider
   */
  override def next():Option[Any] =
  {
    val result = nextBatch(1)
    if(result.isEmpty)
      return None

    val first = result.get.head
    Some(first)
  }


  /**Gets the next batch of items in the queue
   * @param size : The number of items to get at once
   * @return : A list of message object from the underlying queue provider
   */
  override def nextBatch(size:Int = 10):Option[List[Any]] =
  {
    var results:ListBuffer[Any] = null

    execute(SOURCE, "nextbatch", data = Some(size), call = () =>
    {
      val req = new ReceiveMessageRequest(_queueUrl).withMaxNumberOfMessages(size)
      val msgs = _sqs.receiveMessage(req).getMessages
      if(msgs != null && msgs.size() > 0) {
        results = new ListBuffer[Any]()
        for (ndx <- 0 to msgs.size() - 1)
        {
          val msg = msgs.get(ndx)
          results += msg
        }
      }
    })
    if(results == null) return None
    Some(results.toList)
  }


  /** Send a message using either a simple string or a map
    * contains the message data and attributes
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
          var req = new SendMessageRequest(_queueUrl, message)
          if(!Strings.isNullOrEmpty(tagName))
          {
            val finalTagValue = if(Strings.isNullOrEmpty(tagValue)) "" else tagValue
            req = req.addMessageAttributesEntry(tagName, new MessageAttributeValue()
              .withDataType("String").withStringValue(finalTagValue))
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
    path.fold(failure(Some(""), msg = Some("Invalid file path: " + fileNameLocal)))( pathLocal => {
      val content = Files.readAllText(pathLocal)
      send(content, tagName, tagValue)
    })
  }


  /** Abandons the message supplied    *
    * @param item : The message to abandon/delete
    */
  override def abandon(item:Option[Any]):Unit =
  {
    if(item.isEmpty) return
    discard(item.get, "abandon")
  }


  /** Completes the message by deleting it from the queue
    * @param item : The message to complete
    */
  override def complete(item:Option[Any]):Unit =
  {
    if(item.isEmpty) return
    discard(item.get, "complete")
  }


  /** Completes the message by deleting it from the queue
    * @param items : The messages to complete
    */
  override def completeAll(items:Option[List[Any]]):Unit =
  {
    if(items.isEmpty) return
    val all = items.get
    for(item <- all)
    {
      discard(item, "complete")
    }
  }


  override def getMessageBody(msgItem:Option[Any]):String =
  {
    if(msgItem.isEmpty) return Strings.empty
    val msg = msgItem.get
    if(!msg.isInstanceOf[Message]) return Strings.empty
    val sqsMsg = msg.asInstanceOf[Message]
    val body = sqsMsg.getBody
    body
  }


  def getMessageTag(msgItem:Option[Any], tagName:String):String =
  {
    if(msgItem.isEmpty) return Strings.empty
    val msg = msgItem.get
    if(!msg.isInstanceOf[Message]) return Strings.empty
    val sqsMsg = msg.asInstanceOf[Message]
    val atts =  sqsMsg.getAttributes
    if( atts == null) return Strings.empty
    if(!atts.containsKey(tagName)) return Strings.empty
    val tagVal = atts.get(tagName)
    if(tagVal == null) return Strings.empty
    tagVal.toString
  }


  /**Connects to Amazon sqs queue supplied
    */
  protected def connect(credentials:AWSCredentials):Unit =
  {
      val usWest2 = Region.getRegion(Regions.US_WEST_2)
      _sqs = new AmazonSQSClient(credentials)
      _sqs.setRegion(usWest2)
      _queueUrl = _sqs.getQueueUrl(_queue).getQueueUrl
  }


  private def getOrDefault(map:Map[String,Any], key:String, defaultVal:Any): Any =
  {
    if(map == null || !map.contains(key))
      return defaultVal

    map(key)
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
}
