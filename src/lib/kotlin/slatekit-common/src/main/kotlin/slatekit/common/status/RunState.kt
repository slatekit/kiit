/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.status

interface RunState {
    val mode: String
    val value: Int
}

// STATES:
// 1. non-started : not-started
// 2. executing   : currently executing
// 3. waiting     : waiting for work
// 4. paused      : paused for x amount of time before going back to executing
// 5. stopped     : stopped indefinitely
// 6. complete    : completed
// 7. failed      : failed ( opposite of completed )

object RunStateHelpRequest : RunState {
    override val value: Int = 0;
    override val mode: String = "help-request"
}

object RunStateNotStarted : RunState {
    override val value: Int = 1;
    override val mode: String = "not-started"
}

object RunStateInitializing : RunState {
    override val value: Int = 2;
    override val mode: String = "initializing"
}

object RunStateExecuting : RunState {
    override val value: Int = 3;
    override val mode: String = "executing"
}

object RunStateWaiting : RunState {
    override val value: Int = 4;
    override val mode: String = "waiting"
}

object RunStatePaused : RunState {
    override val value: Int = 5;
    override val mode: String = "paused"
}

object RunStateStopped : RunState {
    override val value: Int = 6;
    override val mode: String = "stopped"
}

object RunStateComplete : RunState {
    override val value: Int = 7;
    override val mode: String = "complete"
}

object RunStateFailed : RunState {
    override val value: Int = 8;
    override val mode: String = "failed"
}

