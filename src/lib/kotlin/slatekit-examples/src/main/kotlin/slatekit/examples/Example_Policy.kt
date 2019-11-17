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
import slatekit.functions.limit
import slatekit.functions.retry
import slatekit.functions.every
//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.paged.Pager
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Outcomes
import kotlinx.coroutines.runBlocking
import slatekit.functions.ratio
import slatekit.results.Status

//</doc:import_examples>


class Example_Policy : Command("todo") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>

        runBlocking {

            // Simple circular pager to test the operations.
            // This simply cycles through each item in the list
            val pager = Pager<Int>(listOf(1, 2, 3, 4), false)

            // Case 1: Retry
            println("============================")
            val retryOperation = retry(3, 200) {
                val curr = pager.current(moveNext = true)
                if (curr < 2) {
                    throw Exception("Testing retry at value: $curr")
                }
                println("Retry test: curr=$curr")
                curr
            }
            retryOperation()

            // Case 2: Limit
            println("============================")
            val limitOperation = limit(2) {
                val curr = pager.current(moveNext = true)
                println("Limit test: curr=$curr")
            }
            limitOperation()
            limitOperation()
            limitOperation()

            // Case 3: Every
            println("============================")
            val everyOperation = every(2L, {res -> println("Every test: curr=${res.getOrNull()}") }) {
                val curr = pager.current(moveNext = true)
                Outcomes.success(curr)
            }
            everyOperation()
            everyOperation()
            everyOperation()
            everyOperation()

            // Case 4: Ratio
            println("============================")
            // NOTE: The exact code/msg does not matter, only the type of the Status
            val ratioOperation = ratio(.3, Status.Denied(100, "")) {
                val curr = pager.current(moveNext = true)
                if(curr == 2) Outcomes.denied("test") else Outcomes.success(curr)
            }
            repeat(6){ ratioOperation() }

            println("Done")
        }

        println("done")

        //</doc:examples>
        return Success("")
    }
}

