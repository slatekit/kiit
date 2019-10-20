package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Identity
import slatekit.common.metrics.Calls
import slatekit.common.metrics.Counters
import slatekit.functions.policy.*
import slatekit.results.Codes
import slatekit.results.Outcome
import slatekit.results.Status
import slatekit.results.builders.Outcomes
import slatekit.results.getOrElse

class Policy_Tests {


    @Test
    fun test_limit_success(){
        val counters = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Limit(2, { counters } )
        val result = runBlocking {
            policy.run("1") {
                counters.incProcessed()
                Outcomes.of(it.toInt())
            }
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Status.Succeeded)
        Assert.assertEquals(1, result.getOrNull())
    }


    @Test
    fun test_limit_failure(){
        val counters = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Limit(2, { counters } )
        val result = runBlocking {
            policy.run("1") { counters.incProcessed(); Outcomes.of(it.toInt()) }
            policy.run("2") { counters.incProcessed(); Outcomes.of(it.toInt()) }
            policy.run("3") { counters.incProcessed(); Outcomes.of(it.toInt()) }
        }
        Assert.assertFalse(result.success)
        Assert.assertTrue(result.status is Status.Errored)
        Assert.assertEquals(result.code, Codes.LIMITED.code)
    }


    @Test
    fun test_calls_success(){
        val calls = Calls(Identity.test("policy"))
        val policy:Policy<String,Int> = Calls(2, { calls } )
        val result = runBlocking {
            policy.run("1") {
                calls.inc()
                Outcomes.of(it.toInt())
            }
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Status.Succeeded)
        Assert.assertEquals(1, result.getOrNull())
    }


    @Test
    fun test_calls_failure(){
        val calls = Calls(Identity.test("policy"))
        val policy:Policy<String,Int> = Calls(2, { calls } )
        val result = runBlocking {
            policy.run("1") { calls.inc(); Outcomes.of(it.toInt()) }
            policy.run("2") { calls.inc(); Outcomes.of(it.toInt()) }
            policy.run("3") { calls.inc(); Outcomes.of(it.toInt()) }
        }
        Assert.assertFalse(result.success)
        Assert.assertTrue(result.status is Status.Errored)
        Assert.assertEquals(result.code, Codes.LIMITED.code)
    }


    @Test
    fun test_every_success(){
        var value = -1
        val policy:Policy<String,Int> = Every(2, { i, o -> value = o.getOrElse { -1 }  } )
        val result = runBlocking {
            policy.run("1") { Outcomes.of(it.toInt()) }
            policy.run("2") { Outcomes.of(it.toInt()) }
            policy.run("3") { Outcomes.of(it.toInt()) }
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Status.Succeeded)
        Assert.assertEquals(3, result.getOrNull())
        Assert.assertEquals(2, value)
    }


    @Test
    fun test_ratio_success(){
        val counts = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Ratio(.4, Status.Denied(0, "")) { counts }
        val result = runBlocking {
            policy.run("1") { counts.incProcessed(); counts.incSucceeded(); Outcomes.of(it.toInt()) }
            policy.run("2") { counts.incProcessed(); counts.incDenied(); Outcomes.of(it.toInt()) }
            policy.run("3") { counts.incProcessed(); counts.incSucceeded(); Outcomes.of(it.toInt()) }
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Status.Succeeded)
        Assert.assertEquals(3, result.getOrNull())
    }


    @Test
    fun test_ratio_failure(){
        val counts = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Ratio(.5, Status.Denied(0, "")) { counts }
        val result = runBlocking {
            policy.run("1") { counts.incProcessed(); counts.incSucceeded(); Outcomes.of(it.toInt()) }
            policy.run("2") { counts.incProcessed(); counts.incDenied(); Outcomes.of(it.toInt()) }
            policy.run("3") { counts.incProcessed();counts.incDenied(); Outcomes.of(it.toInt()) }
        }
        Assert.assertFalse(result.success)
        Assert.assertTrue(result.status is Status.Errored)
        Assert.assertEquals(result.code, Codes.LIMITED.code)
    }


    @Test
    fun test_chaining() {
        val id = Identity.test("policy")
        val calls = Calls(id)
        val counts = Counters(id)
        var everyValue = 0
        val policies = listOf<Policy<String, Int>>(
                Limit(4) { i -> counts },
                Calls(3) { i -> calls  },
                Every(2 ) { i, res ->
                    everyValue = res.getOrElse { -1 }
                    println(everyValue)
                },
                Exec { i -> calls.inc(); counts.incProcessed() }
        )

        val last: suspend (String) -> Outcome<Int> = { i -> Outcomes.of(i.toInt()) }
        val flow = Policies.chain(policies, last)
        val result = runBlocking {
            val r1 = flow.invoke("1")
            val r2 = flow.invoke("2")
            r2
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Status.Succeeded)
        Assert.assertEquals(result.code, Codes.SUCCESS.code)
        Assert.assertEquals(2, result.getOrElse { -1 })
        Assert.assertEquals(2, calls.totalRuns())
        Assert.assertEquals(2, counts.totalProcessed())
        Assert.assertEquals(2, everyValue)
    }

}