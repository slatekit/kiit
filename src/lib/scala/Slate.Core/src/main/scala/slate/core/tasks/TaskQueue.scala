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
package slate.core.tasks

import slate.common.Result
import slate.common.queues.QueueSource

/**
  * Task queue is background task/worker pattern to process items in a queue
  * 1. started : starts the task and processes the queue items
  * 2. stopped : stops the task completely
  * 3. paused  : pauses processing of queue for x seconds as specified in settings
  * 4. resumed : resumes processing of queue
  *
  * It also processes the queue items in a batch.
  *
  * OPTIONS:
  * ( See the TaskSettings for more options )
  * 1. you can set the task to run continuously, or run 1 time for the items in the queue.
  * 2. you can set the pause time can be configured
  * 3. you can set the batch size of the items to process from the queue
  * 4. you you can configure the type of queue the task uses.
  *
  * NOTES:
  * 1. Task always processes the queue items in a batch
  * 2. Task will check for stopped/paused state after a batch is completed.
  * 3. Task goes through a life-cycle of events ( init, exec, end )
  * 4. Task can be stopped paused and resumed which changes the internal status of the task
  * 5. You can query the status of the task via the "status()" method.
  *
  * SETUP:
  * 1. You can derive from TaskQueue and override the processItems method
  * 2. You can
  * @param name
  */
class TaskQueue(name:String = "") extends Task(name) {

  var queue:QueueSource = null
  private var _continueRunning = true


  def this(name:String, settings:TaskSettings) = {
    this(name)
    _settings = settings
  }


  /**
   * executes this task by processing items in the queue.
   * this will wait / sleep for x seconds ( as configured ) if this task
   * is paused or stopped.
    *
    * @return
   */
  override protected def onExec():Result[Any] =
  {
    _continueRunning = true
    while(_continueRunning) {

      // CASE 1: Running
      if (isStartedResumedWaiting() ){
        process()
      }
      // CASE 2: Paused by caller: sleep for x seconds
      else if (_settings.isOngoing && isPaused()) {
        sleep()
        resume()
      }
      // CASE 3: Stopped by caller
      else if (_settings.isOngoing && isStopped()) {
        _continueRunning = false
      }
      // CASE 4: Waiting / so sleep for x seconds
      else if (_settings.isOngoing && isWaiting()) {
        waiting()
        sleep()
      }
    }
    success("execution complete")
  }


  protected def process(): Unit = {

    // Get items from queue
    val items = queue.nextBatch(_settings.batchSize)
    val anyItems = items.isDefined && items.get.length > 0

    // CASE 1: Items exist - process
    if ( _settings.isOngoing && anyItems ) {

      // status update
      statusExecuting()

      // process
      processItems(items)

      // take a break
      if(_settings.pauseAfterProcessing) {
        sleep()
        resume()
      }
    }
    // CASE 2: No items, wait for x seconds if on-going
    else if ( _settings.isOngoing && !anyItems ) {
      waiting()
      sleep()
    }
    // CASE 1c: 1 time run and no items!
    else if ( !_settings.isOngoing && !anyItems ) {
      _continueRunning = false
    }
  }


  /**
   * iterates through the batch of items and processes each one, and completes it
   * if successful, or abandons it on failure.
    *
    * @param items
   */
  protected def processItems(items:Option[List[Any]]):Unit = {

    // CASE 1: any ?
    if (items.isDefined){

      // handle each one
      if(items.get.size > 0) {

        for (item <- items.get) {

          // avoid failure ( either complete/abandon item )
          try {
            processItem(item)
            queue.complete(Some(item))
          }
          catch {
            case ex: Exception => {
              queue.abandon(Some(item))
            }
          }
        }
      }
    }
  }


  /**
   * processes a single item. derived classes should implement this.
    *
    * @param item
   */
  protected def processItem(item:Any): Unit = {
    // implement logic here.
  }


  protected def sleep():Unit = {
    var seconds = _pauseSeconds
    if(seconds == 0 && _settings != null ){
      seconds = _settings.pauseTimeInSeconds
    }
    if(seconds == 0){
      seconds = 15
    }
    sleep(Some(seconds))
  }


  protected def sleep(seconds:Option[Int]): Unit = {
    val secs = seconds.getOrElse(_settings.waitTimeInSeconds)
    val msSeconds = secs * 1000
    Thread.sleep(msSeconds)
  }
}
