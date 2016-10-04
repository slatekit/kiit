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

package slate.ext.devops

import slate.cloud.aws.{AwsCloudFiles, AwsCloudQueue}
import slate.common.queues.QueueSource
import slate.common.{Env, Ioc}
import slate.core.apis.{ApiAction, Api}
import slate.core.common.svcs.ApiWithSupport
import slate.ext.tasks.TaskService

@Api(area = "app", name = "setup", desc = "api info about the application and host",
  roles= "admin", auth = "key", verb = "post", protocol = "cli")
class SetupApi
  extends ApiWithSupport
{

  @ApiAction(name = "", desc= "create instance of aws sqs queue with supplied name", roles= "@parent", verb = "@parent", protocol = "@parent")
  def sqs(name:String):Unit = {

    val queue = new AwsCloudQueue(context.cfg.getStringEnc( Env.name + ".aws.sqs.name"))
    queue.connectWith(context.cfg.getStringEnc("aws.key"), context.cfg.getStringEnc("aws.pswd"))
    Ioc.register("que", queue)
  }


  @ApiAction(name = "", desc= "create instance of aws s3 with supplied name", roles= "@parent", verb = "@parent", protocol = "@parent")
  def s3(name:String):Unit =
  {
    val files = new AwsCloudFiles(context.cfg.getStringEnc( Env.name + ".aws.s3.name"), false)
    files.connectWith(context.cfg.getStringEnc("aws.key"), context.cfg.getStringEnc("aws.pswd"))
    Ioc.register("files", files)
  }


  @ApiAction(name = "", desc= "create instance of the push notifications", roles= "@parent", verb = "@parent", protocol = "@parent")
  def taskQueue(task:String, queue:String):Unit =
  {
    val svc = Ioc.get(task).get.asInstanceOf[TaskService]
    val que = Ioc.get(queue).get.asInstanceOf[QueueSource]
    svc.setQueue(que)
  }
}
