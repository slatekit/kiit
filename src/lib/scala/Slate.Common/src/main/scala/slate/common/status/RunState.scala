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
}

case object RunStateHelpRequest  extends RunState { val mode:String = "help-request" }
case object RunStateNotStarted   extends RunState { val mode:String = "not-started"  }
case object RunStateInitializing extends RunState { val mode:String = "initializing" }
case object RunStateStarted      extends RunState { val mode:String = "started"      }
case object RunStateExecuting    extends RunState { val mode:String = "executing"    }
case object RunStateWaiting      extends RunState { val mode:String = "waiting"      }
case object RunStatePaused       extends RunState { val mode:String = "paused"       }
case object RunStateResumed      extends RunState { val mode:String = "resumed"      }
case object RunStateStopped      extends RunState { val mode:String = "stopped"      }
case object RunStateEnded        extends RunState { val mode:String = "ended"        }

