/**
<slate_header>
author: Kishore Reddy
url: www.github.com/code-helix/slatekit
copyright: 2015 Kishore Reddy
license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
desc: A tool-kit, utility library and server-backend
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.examples


//<doc:import_required>
import slatekit.common.*
import slatekit.tracking.Calls
import slatekit.tracking.Counters
//</doc:import_required>

//<doc:import_examples>
import slatekit.results.Try
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.Success

//</doc:import_examples>


class Example_Tracking : Command("auth") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>
        // Setup 1: The calls component with an Identity ( could be name of a function, job, etc )
        // This is just a simple counter for calls and only stores a few states ( passed / failed / total )
        val calls = Calls(Identity.test("calls"))

        // Setup 2: The calls component with an Identity ( could be name of a function, job, etc )
        // This is just a simple counter for calls and only stores a few states ( passed / failed / total )
        val counts = Counters(Identity.test("job1"))
        //</doc:setup>

        //<doc:examples>
        // Use case 1.1: Increment attempt
        calls.inc()

        // Use case 1.2: Increment number of passed calls
        calls.passed()

        // Use case 1.3: Increment number of failed calls
        calls.failed()

        // Use case 1.4: Get the total number of times the operation was run
        calls.totalRuns()

        // Use case 1.4a: Get the last error
        calls.totalPassed()

        // Use case 1.4b: Get the last error
        calls.totalFailed()

        // Use case 1.5: Determine if operation has run ( if totalPassed > 0 )
        calls.hasRun()

        // Use case 1.6: Get the last time the call was made
        calls.lastTime()

        // Use case 1.7: Get the last error
        calls.lastError()

        // COMPONENT: Counters
        // Use case 2.1: Increment total processed
        counts.incProcessed()

        // Use case 2.2: Increment total succeeded
        counts.incSucceeded()

        // Use case 2.3: Increment total denied
        counts.incDenied()

        // Use case 2.3: Increment total invalid
        counts.incInvalid()

        // Use case 2.5: Increment total ignored
        counts.incIgnored()

        // Use case 2.6: Increment total errored
        counts.incErrored()

        // Use case 2.7: Increment total unexpected
        counts.incUnexpected()

        // Use case 2.8: Increment total processed
        counts.decProcessed()
        counts.decSucceeded()
        counts.decDenied()
        counts.decInvalid()
        counts.decIgnored()
        counts.decErrored()
        counts.decUnexpected()

        //</doc:examples>

        return Success("Ok")
    }
}
