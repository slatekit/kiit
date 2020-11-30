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
//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.log.LoggerConsole
import slatekit.results.*
import slatekit.results.builders.Outcomes
import slatekit.tracking.*

//</doc:import_examples>


class Example_Tracking : Command("auth") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>
        //</doc:setup>

        //<doc:examples>
        /** COMPONENT: Calls
         * The calls component with an Identity ( could be name of a function, job, etc )
         * is just a simple counter for calls/operation and only stores a few states ( passed / failed / total )
         * */
        val id = SimpleIdentity("beta", "setup", Agent.Job, "dev")
        val calls = Calls(id)

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

        /** COMPONENT: Counters
         * Slate Kit heavily uses logical success / failure categories @see[slatekit.results.Status]
         * 1. Succeeded   -> Success
         * 2. Pending     -> In Progress
         * 3. Denied      -> Security related
         * 4. Ignored     -> Ignored items
         * 5. Invalid     -> Invalid data
         * 6. Errored     -> Expected errors
         * 7. Unexpected  -> Unexpected errors
         * This counters component keeps track of various categories
         */
        val counts = Counters(id, custom = listOf("deferred"))

        // Use case 2.1: Increment total processed
        counts.processed.inc()
        counts.succeeded.inc()
        counts.denied.inc()
        counts.invalid.inc()
        counts.ignored.inc()
        counts.errored.inc()
        counts.unknown.inc()
        counts.inc("deferred")

        // Use case 2.2: Increment total processed
        counts.processed.dec()
        counts.succeeded.dec()
        counts.denied.dec()
        counts.invalid.dec()
        counts.ignored.dec()
        counts.errored.dec()
        counts.unknown.dec()
        counts.dec("deferred")

        // Use case 2.3: Get totals
        counts.processed.get()
        counts.succeeded.get()
        counts.denied.get()
        counts.invalid.get()
        counts.ignored.get()
        counts.errored.get()
        counts.unknown.get()
        counts.get("deferred")

        // The next 2 examples involve tracking and handling requests/results.
        // Lets setup some example request / result types
        data class UserRequest(val id:String, val action:String)
        data class UserResult (val id:String, val action:String, val msg:String)
        val sampleRequest = UserRequest("uuid123", "register")
        val sampleResult = UserResult("uuid123", "registration", "registered as beta user")

        /** COMPONENT: Lasts
         * The event class provides a way to store the last occurrence of a specific request / result
         */
        // Use case 3.1: Track various states
        val lasts = Lasts<UserRequest, UserResult, Err>(Identity.test("job1"))
        lasts.succeeded (this, sampleRequest, sampleResult)
        lasts.denied    (this, sampleRequest, Err.of("Not authorized"))
        lasts.invalid   (this, sampleRequest, Err.of("Not a beta user"))
        lasts.ignored   (this, sampleRequest, Err.of("In active user"))
        lasts.errored   (this, sampleRequest, Err.of("Unable to determine user type"))
        lasts.unexpected(this, sampleRequest, Err.of("Unexpected error while handling analytics"))

        // Use case 3.2: Get info
        println(lasts.lastSuccess())
        println(lasts.lastDenied())
        println(lasts.lastInvalid())
        println(lasts.lastIgnored())
        println(lasts.lastErrored())
        println(lasts.lastUnexpected())

        // Use case 3.3: DeleteAll the tracking
        lasts.clear()

        /** COMPONENT: Event
         * The event class provides a way to generically represent an occurred Event with some relevant info.
         * You can convert almost any item/request to an Event with custom fields
         * You can then use this event for structured logging, alerts
         */
        // Use case 4.1: Create a sample registration event
        val event = Event(
                area = "registration",
                name = "NEW_ANDROID_REGISTRATION",
                agent = "job",
                env = "dev",
                uuid = "abc-123-xyz",
                desc = "User registration via mobile",
                status = Codes.SUCCESS,
                target = "registration-alerts",
                tag = "a1b2c3",
                fields = listOf(
                        Triple("region", "usa", ""),
                        Triple("device", "android", "")
                )
        )

        /** Setup a simple Events class that can handle Requests/Results/Failures of type
         * request = UserRequest, result = UserResult, error = @see[slatekit.results.Err]
         * This also needs to be able to convert the request / result / failure to an event.
         */
        val events = Events<UserRequest, UserResult, Err>(
                successConverter = { sender, req, res -> event.copy(uuid = res.id, desc = res.msg)},
                failureConverter = { sender, req, err -> event.copy(uuid = req.id, desc = "Failed registering beta user", status = err.status)}
        )

        // Use case 4.2: Model a sample request and result using slatekit.results.Outcome and have the handler
        // process it accordingly based on its category
        events.handle(this, sampleRequest, Outcomes.success(sampleResult))
        events.handle(this, sampleRequest, Outcomes.denied(Err.of("Not authorized")))
        events.handle(this, sampleRequest, Outcomes.invalid(Err.of("Not a beta user")))
        events.handle(this, sampleRequest, Outcomes.ignored(Err.of("In active user")))
        events.handle(this, sampleRequest, Outcomes.errored(Err.of("Unable to determine user type")))
        events.handle(this, sampleRequest, Outcomes.unexpected(Err.of("Unexpected error while handling analytics")))

        /** COMPONENT: Recorder
         * The recorder component is a combination of all the above.
         */
        val recorder = Recorder<UserRequest, UserResult>(Identity.test("job1"), LoggerConsole(), calls, counts, lasts, events)

        // Use case 5: Record the request / result in the recorder which will track the call, counts, lasts, and events
        recorder.record(this, sampleRequest, Outcomes.success(sampleResult))
        recorder.record(this, sampleRequest, Outcomes.denied(Err.of("Not authorized")))
        recorder.record(this, sampleRequest, Outcomes.invalid(Err.of("Not a beta user")))
        recorder.record(this, sampleRequest, Outcomes.ignored(Err.of("In active user")))
        recorder.record(this, sampleRequest, Outcomes.errored(Err.of("Unable to determine user type")))
        recorder.record(this, sampleRequest, Outcomes.unexpected(Err.of("Unexpected error while handling analytics")))

        // Initial settings
        data class UserSettings(val userId:String, val isBetaTester:Boolean)
        val settings = UserSettings("user1", false)

        // Track as updates
        val updates = Updates.of(settings)
        val update1 = updates.set(settings)
        val update2 = update1.map { it.copy(isBetaTester = true) }

        println(update2.created) // initial creation time
        println(update2.updated) // last update time
        println(update2.applied) // total times changed
        println(update2.current) // current value

        //</doc:examples>

        return Success("Ok")
    }
}
