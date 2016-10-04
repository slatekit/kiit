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

import slate.common.Strings
import slate.common.queues.QueueSourceMsg
import slate.core.mobile.Message
import slate.core.tasks.{TaskSettings, TaskQueue}

class MessageTaskQueue(val msgService:MessageUserService, name:String, settings:TaskSettings)
  extends TaskQueue(name, settings) {

  /**
    * processes a single item.
    *
    * @param item
    */
  override protected def processItem(item:Any): Unit = {
    val content = queue.asInstanceOf[QueueSourceMsg].getMessageBody(Some(item))

    // Message exists
    if(!Strings.isNullOrEmpty(content)){

      val msg = Message.fromJson(content)
      msgService.send(msg)
    }
  }
}
