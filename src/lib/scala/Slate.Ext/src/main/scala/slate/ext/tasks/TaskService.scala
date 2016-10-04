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

import slate.common.Result
import slate.common.app.AppRunState
import slate.common.queues.QueueSource
import slate.common.results.{ResultSupportIn}
import slate.core.tasks.{TaskRunner, TaskQueue}

class TaskService(var task:Option[TaskQueue] = None) extends ResultSupportIn{
  private var _started = false
  private var _name = ""
  private var _tag = ""


  def start(name:String, tag:String):Result[AppRunState] =
  {
    // Checks
    if(!task.isDefined) {
      return failure(Some("task has not been defined"))
    }

    synchronized {
      _name = name
      _tag = tag
      TaskRunner.run(task.get, List[String](_name, _tag))
      _started = true
    }
    success(task.get.status(), Some("started"))
  }


  def stop():Result[AppRunState] =
  {
    perform( (task) => task.stop() )
  }


  def pause():Result[AppRunState] =
  {
    perform( (task) => task.pause() )
  }


  def pauseSeconds(seconds:Int):Result[AppRunState] =
  {
    perform( (task) => task.pause(seconds) )
  }


  def resume():Result[AppRunState] =
  {
    perform( (task) => task.resume() )
  }


  def status():Result[AppRunState] =
  {
    perform( (task) => task.status() )
  }


  def send(message:String):Result[AppRunState] =
  {
    perform( (task) => task.queue.send(message) )
  }


  def setQueue(queue:QueueSource):Result[AppRunState] =
  {
    perform( (task) => task.queue = queue )
  }


  private def perform(callback:(TaskQueue) => Unit ): Result[AppRunState] = {

    synchronized {
      if (!_started) {
        return failure[AppRunState](Some("Task has not been started"))
      }
      callback(task.get)
    }
    success(task.get.status(), Some("task operation succeded"))
  }
}
