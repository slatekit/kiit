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
package slate.ext.tasks

import slate.common.app.AppRunState
import slate.common.{IocRunTime, Result}
import slate.core.apis.{ApiAction, Api}
import slate.core.common.svcs.ApiWithSupport

@Api(area = "app", name = "tasks", desc = "api to manage the task",
  roles= "@admin", auth = "app", verb = "*", protocol = "*")
class TaskApi
  extends ApiWithSupport
{

  @ApiAction(name = "", desc= "starts the task or resume it", roles= "")
  def start(name:String, tag:String):Result[AppRunState] =
  {
    perform(name, (task) => task.start(name, tag) )
  }


  @ApiAction(name = "", desc= "stops the task", roles= "")
  def stop(name:String):Result[AppRunState] =
  {
    perform(name, (task) => task.stop() )
  }


  @ApiAction(name = "", desc= "pauses the task", roles= "")
  def pause(name:String):Result[AppRunState] =
  {
    perform(name, (task) => task.pause() )
  }


  @ApiAction(name = "", desc= "pauses the task", roles= "")
  def pauseSeconds(name:String, seconds:Int):Result[AppRunState] =
  {
    perform(name, (task) => task.pauseSeconds(seconds) )
  }


  @ApiAction(name = "", desc= "resumes the task", roles= "")
  def resume(name:String):Result[AppRunState] =
  {
    perform(name, (task) => task.resume() )
  }


  @ApiAction(name = "", desc= "gets the status of the running task", roles= "")
  def status(name:String):Result[AppRunState] =
  {
    perform(name, (task) => task.status() )
  }


  @ApiAction(name = "", desc= "gets the status of the running task", roles= "")
  def send(name:String, message:String):Result[AppRunState] =
  {
    perform(name, (task) => task.send(message) )
  }


  def service(name:String): Option[TaskService] = {
    getSvc[TaskService](name)
  }


  private def perform(name:String, callback:(TaskService) => Result[AppRunState] ): Result[AppRunState] = {
    val svcCheck = service(name)
    if(!svcCheck.isDefined)
      return failure[AppRunState](msg = Some("Task not setup"))
    callback(svcCheck.get)
  }
}
