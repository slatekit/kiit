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


package slate.examples

//<doc:import_required>
import slate.core.tasks.{TaskRunner, TaskQueue, Task}
//</doc:import_required>

//<doc:import_examples>
import slate.common.app.AppMeta
import slate.common.results.ResultSupportIn
import slate.common.Result
import slate.common.info.About
import slate.common.queues.QueueSourceDefault
import slate.core.cmds.Cmd
//</doc:import_examples>



/**
 * Created by kv on 11/4/2015.
 */
class Example_Task extends Cmd("types") with ResultSupportIn {

  //<doc:setup>
  // Setup 1: This is a simple task that extends from task
  // It has built in support for
  // 1. life-cycle events for init, exec, end.
  // 2. status updates
  // 3. support for setting/getting app info
  class SampleTask1 extends Task
  {
    _appMeta = new AppMeta(new About(
      id = "slatekit.task",
      name = "Sample Slate Task",
      desc = "Sample to show creating a simple task",
      company = "slatekit",
      region = "ny",
      version = "0.9.1",
      url = "http://www.slatekit.com",
      group = "codehelix.co",
      contact = "kishore@codehelix.co",
      tags = "slate,shell,cli"
    ))


    override protected def onInit(args:Option[Any]): Result[Boolean] = {
      ok()
    }


    override protected def onExec():Result[Any] =
    {
      Thread.sleep(2000)
      success(true)
    }


    override protected def onEnd() : Unit =
    {
    }
  }


  // Setup 2: Create a new task that is a long running background task
  // that processes messages in a queue.
  // NOTE: The queue can be any type of queue ( AWS-SQS, default queue )
  val task2 = new TaskQueue("sample task")
  task2.queue = new QueueSourceDefault()

  // Send some messages to the in-memory default queue.
  for(i <- 1 to 10 ){
    task2.queue.send(i)
  }
  //</doc:setup>

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // CASE 1: Start in background thread
    TaskRunner.run(task2, null)

    // CASE 2: Pause the task
    task2.pause()
    task2.isPaused()

    // CASE 3: Resume the task
    task2.resume()
    task2.isResumed()
    task2.isStartedOrResumed()

    // CASE 4: Stop the task and ends the background thread
    task2.stop()
    task2.isStopped()

    // CASE 5: Starts the task again ( via background thread )
    TaskRunner.run(task2, null)
    task2.isStarted()

    // CASE 6: Check if task is waiting for messages on queue
    task2.isWaiting()
    //</doc:examples>

    ok()
  }
}
