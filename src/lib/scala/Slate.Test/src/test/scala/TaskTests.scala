import slate.tests.common.ServiceFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import slate.common.app.AppRunConst
import slate.common.queues.QueueSourceDefault
import slate.core.tasks.{TaskRunner, TaskQueue}

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

class TaskTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll{
  private var count = 0


  override def beforeAll() {
    println("before all")
  }

  override def afterAll() {
    println("after all")
  }


  def counter():Int = { count = count+1; count;}

  before {
    println("before: " + counter())
  }


  after {
    println("after: " + counter())
  }


  test("can move states") {
    val task = new TaskQueue()
    task.start()
    assert(task.status().status == AppRunConst.STARTED)
    assert(task.isStarted())
    assert(task.isStartedOrResumed())

    task.pause(30)
    assert(task.status().status == AppRunConst.PAUSED)
    assert(task.isPaused())
    assert(task.isStoppedOrPaused())

    task.resume()
    assert(task.status().status == AppRunConst.RESUMED)
    assert(task.isResumed())
    assert(task.isStartedOrResumed())

    task.stop()
    assert(task.status().status == AppRunConst.STOPPED)
    assert(task.isStopped())
    assert(task.isStoppedOrPaused())

    task.waiting()
    assert(task.status().status == AppRunConst.WAITING)
    assert(task.isWaiting())
  }


  test("can pause") {
    //val task = buildTask()
    //TaskRunner.run(task)
    //Thread.sleep(3000)
    //task.pause()
    //Thread.sleep(3000)
    //assert(task.isPaused())
  }


  test("can pause resume") {
  }


  test("can start") {
  }


  test("can start resume") {
  }


  private def buildTask():TaskQueue = {
    val queue = new QueueSourceDefault()
    queue.connect(null)
    for(ndx <-1 to 10) {
      queue.send(s"msg : ${ndx}")
    }
    //val task = new TaskQueue(new TaskSettings(1, true, 5, 5, 60, true))
    //task.queue = queue
    //task
    null
  }
}
