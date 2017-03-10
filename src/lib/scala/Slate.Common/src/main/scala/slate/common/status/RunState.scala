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

package slate.common.status

trait RunState {
  val mode:String
  val value:Int
}

// STATES:
// 1. non-started : not-started
// 2. executing   : currently executing
// 3. waiting     : waiting for work
// 4. paused      : paused for x amount of time before going back to executing
// 5. stopped     : stopped indefinitely
// 6. complete    : completed
// 7. failed      : failed ( opposite of completed )

case object RunStateHelpRequest  extends RunState { val value:Int = 0; val mode:String = "help-request" }
case object RunStateNotStarted   extends RunState { val value:Int = 1; val mode:String = "not-started"  }
case object RunStateInitializing extends RunState { val value:Int = 2; val mode:String = "initializing" }
case object RunStateExecuting    extends RunState { val value:Int = 3; val mode:String = "executing"    }
case object RunStateWaiting      extends RunState { val value:Int = 4; val mode:String = "waiting"      }
case object RunStatePaused       extends RunState { val value:Int = 5; val mode:String = "paused"       }
case object RunStateStopped      extends RunState { val value:Int = 6; val mode:String = "stopped"      }
case object RunStateComplete     extends RunState { val value:Int = 7; val mode:String = "complete"     }
case object RunStateFailed       extends RunState { val value:Int = 8; val mode:String = "failed"       }

