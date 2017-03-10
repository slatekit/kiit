import java.util.concurrent.atomic.AtomicReference

import slate.common.info._
import slate.common.status._
import slate.tests.common.ServiceFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import slate.common.app.AppMeta
import slate.common.queues.QueueSourceDefault
import slate.core.tasks.{TaskSettings, TaskQueue}

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


  test("can use atomic"){
    val r = new AtomicReference[RunState](RunStateNotStarted)
    assert( r.get() == RunStateNotStarted)

    val v1 = r.get()
    r.set(RunStateInitializing)
    assert( v1 != r.get() )
  }


  test("can move states") {
    val meta = AppMeta(About.none, Host.local(), Lang.asScala(), Status.none, StartInfo.none)
    val task = new TaskQueue("", new TaskSettings(), meta, None, new QueueSourceDefault())
    task.start()
    assert(task.status().status == RunStateExecuting.mode)
    assert(task.isStarted())
    assert(task.isExecuting())

    task.pause(30)
    assert(task.status().status == RunStatePaused.mode)
    assert(task.isPaused())
    assert(task.isStoppedOrPaused())

    task.resume()
    assert(task.status().status == RunStateExecuting.mode)
    assert(task.isStarted())
    assert(task.isExecuting())

    task.stop()
    assert(task.status().status == RunStateStopped.mode)
    assert(task.isStopped())
    assert(task.isStoppedOrPaused())

    task.waiting()
    assert(task.status().status == RunStateWaiting.mode)
    assert(task.isWaiting())
  }


  test("can pause") {
  }


  test("can pause resume") {
  }


  test("can start") {
  }


  test("can start resume") {
  }


  private def buildTask():TaskQueue[_] = {
    val queue = new QueueSourceDefault()
    for(ndx <-1 to 10) {
      queue.send(s"msg : ${ndx}")
    }
    val task = new TaskQueue[Any]("sample", new TaskSettings(1, true, 5, 5, 60), AppMeta.none, None, queue)
    task
  }
}
