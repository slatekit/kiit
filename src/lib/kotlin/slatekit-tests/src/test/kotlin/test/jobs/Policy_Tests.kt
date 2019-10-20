package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Test
import slatekit.common.Identity
import slatekit.common.metrics.Calls
import slatekit.common.metrics.Counters
import slatekit.functions.policy.*
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

class Policy_Tests {


    @Test
    fun test() {
        val id = Identity.test("")
        val counts = Counters(Identity.test(""))
        val policies = listOf<Policy<Int, String>>(
                Limit(10) { i -> counts },
                Calls(2) { i -> Calls(id) },
                Every(2 ) { i, res -> println(res)  },
                Exec()
        )

        val req = 123
        val items = listOf(1, 2, 3)
        val f = items.fold("start") { acc, a ->
            acc + "," + a
        }

        val t = runBlocking {
            policies.first().run(req) {
                policies[1].run(it) {
                    policies[2].run(it) {
                        Outcomes.of(it.toString())
                    }
                }
            }
        }

        val last: suspend (Int) -> Outcome<String> = { i -> Outcomes.of("Executing : $i") }
        val flow = Policies.chain(policies, last)
        val result = runBlocking {
            flow.invoke(req)
        }
        println(result)
    }

}