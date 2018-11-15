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

package slatekit.workers.status


// STATES:
// 1. non-started : not-started
// 2. executing   : currently executing
// 3. waiting     : waiting for work
// 4. paused      : paused for x amount of time before going back to executing
// 5. stopped     : stopped indefinitely
// 6. complete    : completed
// 7. failed      : failed ( opposite of completed )
sealed class Status(val name:String, val value:Int) {
    object InActive : Status("InActive", 0)
    object Starting : Status("Starting", 0)
    object Idle     : Status("Idle"    , 1)
    object Running  : Status("Running" , 2)
    object Paused   : Status("Paused"  , 3)
    object Stopped  : Status("Stopped" , 4)
    object Complete : Status("Complete", 5)
    object Failed   : Status("Failed"  , 6)
}
