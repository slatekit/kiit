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
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.Identity
import slatekit.common.paged.Pager
import slatekit.functions.limit
import slatekit.functions.retry
import slatekit.functions.every
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Outcomes
import slatekit.tracking.Counters
import java.util.concurrent.atomic.AtomicInteger

//</doc:import_examples>


class Example_Policy : Command("todo") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>

        runBlocking {

            // Case 1: Retry
            println("=".repeat(20))
            val pager1 = Pager<Int>(listOf(1, 2, 3, 4), false)
            retry(3, 200) {
                val curr = pager1.current()
                pager1.next()
                if (curr < 2) {
                    throw Exception("Testing retry at value: $curr")
                }
                println("Retry test: curr=$curr")
                curr
            }

            // Case 2: Limit
            println("=".repeat(20))
            val pager2 = Pager<String>(listOf("a", "b", "c", "d"), false)
            val limitOperation = limit(2) {
                val curr = pager2.current()
                pager2.next()
                println("Limit test: curr=$curr")
            }
            limitOperation()
            limitOperation()
            limitOperation()

            // Case 3: Every
            println("=".repeat(20))
            val pager3 = Pager<String>(listOf("a", "b", "c", "d"), false)
            val everyOperation = every(2L, {res -> println("Every test: curr=${res.getOrNull()}") }) {
                val curr = pager3.current()
                pager3.next()
                Outcomes.success(curr)
            }
            everyOperation()
            everyOperation()
            everyOperation()
            everyOperation()

        }

        println("done")

        //</doc:examples>
        return Success("")
    }
}

