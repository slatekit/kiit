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
package slate.ext.mobile

import slate.common.encrypt.Encryptor
import slate.common.{DateTime, Strings, Result}
import slate.common.queues.{QueueSourceMsg, QueueSource}
import slate.common.results.{ResultSupportIn}
import slate.core.mobile.{Message, MessageService}
import slate.ext.users.{User, UserHelper, UserService}

class MessageUserService(private val _users:UserService,
                         private val _encryptor:Encryptor,
                         queue:Option[QueueSource] = None)
  extends MessageService(queue) with ResultSupportIn {


  def share(userId:String, destId:String, title:String, details:String, data:String, date:DateTime,
            msgType:String, msgAction:String, msgTag:String):Unit = {
    val msg = Message.share(msgType, msgAction)
    msg.message(title, details, data, date)
    sendInternal(userId, destId, msg, false)
  }


  def alert(userId:String, destId:String, title:String, details:String, data:String, date:DateTime):Unit = {
    val msg = Message.alert()
    msg.message(title, details, data, date)
    sendInternal(userId, destId, msg, false)
  }


  def msg(userId:String, destId:String, title:String, details:String, data:String, date:DateTime):Unit = {
    val msg = Message.msg()
    msg.message(title, details, data, date)
    sendInternal(userId, destId, msg, false)
  }


  /**
   * send message to the destination id which can be either a username, phone, email.
   * @param id     : id of the sender( userId )
   * @param destId : user, phone, email of recipient. ( "phone:123456789", "email:a@abc.com", @user")
   * @param msg
   * @return
   */
  override def send(id:String, destId:String, msg:Message): Result[Boolean] = {
    sendInternal(id, destId, msg, false)
    ok()
  }


  private def sendInternal(id:String, destId:String, msg:Message, immediate:Boolean): Result[Boolean] = {

    // Get sender user
    val decrypted = _encryptor.decrypt(id)
    val userId = UserHelper.parseUserId(decrypted)
    val userCheck = _users.getByUserId(userId.guid)

    // Unknown user ?
    if (!userCheck.isDefined)
      return unAuthorized[Boolean](Some("Unknown sender"))

    // Validate message
    val result = validate(msg, userCheck.get)
    if (!result.success)
      return failure(result.msg)

    // Get destination user ( either email, phone, id, reference to self )
    val destCheck = _users.getUserById(destId, Some(destId), userCheck)

    // unknown destination ?
    if (!destCheck.isDefined)
      return failure[Boolean](Some("Unknown user : " + destId))

    // Fill message details
    val user = userCheck.get
    val dest = destCheck.get
    msg.source("server", user.country)
    msg.from(user.userName, user.primaryPhone, user.fullUserId().toString)
    msg.to(dest.id.toString, dest.primaryPhone, dest.userName,
      dest.primaryPhonePlatform, dest.primaryPhoneRegId)

    // queue the message.
    if(immediate){
      send(msg)
    }
    else {
      queue(msg)
    }

    ok()
  }


  /**
    * processes a single item.
    *
    * @param item
    */
  def processItem(item:Any): Unit = {
    val content = queue.asInstanceOf[QueueSourceMsg].getMessageBody(Some(item))

    // Message exists
    if(!Strings.isNullOrEmpty(content)){

      val msg = Message.fromJson(content)
      send(msg)
    }
  }


  def validate(msg:Message, user:User):Result[Boolean] = {
    if (Strings.isNullOrEmpty(msg.subject))  return failure[Boolean](Some("message subject not supplied"))
    if (Strings.isNullOrEmpty(msg.data))     return failure[Boolean](Some("message data not supplied"))
    ok()
  }
}
