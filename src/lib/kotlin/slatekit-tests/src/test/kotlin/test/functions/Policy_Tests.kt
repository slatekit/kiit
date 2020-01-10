package test.functions

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Identity
import slatekit.tracking.Calls
import slatekit.tracking.Counters
import slatekit.functions.policy.*
import slatekit.results.*
import slatekit.results.builders.Outcomes

class Policy_Tests {


    @Test
    fun test_limit_success(){
        val counters = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Limit(2, true, { counters } )
        val result = runBlocking {
            policy.run("1") {
                Outcomes.of(it.toInt())
            }
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Passed.Succeeded)
        Assert.assertEquals(1, result.getOrNull())
    }


    @Test
    fun test_limit_failure(){
        val counters = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Limit(2, true, { counters } )
        val result = runBlocking {
            policy.run("1") { Outcomes.of(it.toInt()) }
            policy.run("2") { Outcomes.of(it.toInt()) }
            policy.run("3") { Outcomes.of(it.toInt()) }
        }
        Assert.assertFalse(result.success)
        Assert.assertTrue(result.status is Failed.Errored)
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
        Assert.assertTrue(result.status is Passed.Succeeded)
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
        Assert.assertTrue(result.status is Failed.Errored)
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
        Assert.assertTrue(result.status is Passed.Succeeded)
        Assert.assertEquals(3, result.getOrNull())
        Assert.assertEquals(2, value)
    }


    @Test
    fun test_ratio_success(){
        val counts = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Ratio(.4, Failed.Denied(0, ""), { counts })
        val result = runBlocking {
            policy.run("1") { counts.incProcessed(); counts.incSucceeded(); Outcomes.of(it.toInt()) }
            policy.run("2") { counts.incProcessed(); counts.incDenied(); Outcomes.of(it.toInt()) }
            policy.run("3") { counts.incProcessed(); counts.incSucceeded(); Outcomes.of(it.toInt()) }
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Passed.Succeeded)
        Assert.assertEquals(3, result.getOrNull())
    }


    @Test
    fun test_ratio_failure(){
        val counts = Counters(Identity.test("policy"))
        val policy:Policy<String,Int> = Ratio(.5, Failed.Denied(0, ""), { counts })
        val result = runBlocking {
            policy.run("1") { counts.incProcessed(); counts.incSucceeded(); Outcomes.of(it.toInt()) }
            policy.run("2") { counts.incProcessed(); counts.incDenied(); Outcomes.of(it.toInt()) }
            policy.run("3") { counts.incProcessed();counts.incDenied(); Outcomes.of(it.toInt()) }
        }
        Assert.assertFalse(result.success)
        Assert.assertTrue(result.status is Failed.Errored)
        Assert.assertEquals(result.code, Codes.LIMITED.code)
    }


    @Test
    fun test_compose() {
        val id = Identity.test("policy")
        val calls = Calls(id)
        val counts = Counters(id)
        val p1 = Limit<String, Int>(4, true, { i -> counts })
        val p2 = Calls<String, Int>(3, { i -> calls  })

        val exec = Policies.compose(p2) { i -> calls.inc(); Outcomes.of(i.toInt() )}
        val call = Policies.compose(p1, exec)
        val result = runBlocking { call("1"); call("2"); }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Passed.Succeeded)
        Assert.assertEquals(result.code, Codes.SUCCESS.code)
        Assert.assertEquals(2, result.getOrElse { -1 })
        Assert.assertEquals(2, calls.totalRuns())
        Assert.assertEquals(2, counts.totalProcessed())
    }


    @Test
    fun test_chaining() {
        val id = Identity.test("policy")
        val calls = Calls(id)
        val counts = Counters(id)
        var everyValue = 0
        val policies = listOf<Policy<String, Int>>(
                Limit(4, true, { i -> counts }),
                Calls(3, { i -> calls  }),
                Every(2, { i, res ->
                    everyValue = res.getOrElse { -1 }
                    println(everyValue)
                }),
                Exec { i -> calls.inc(); }
        )

        val last: suspend (String) -> Outcome<Int> = { i -> Outcomes.of(i.toInt()) }
        val flow = Policies.chain(policies, last)
        val result = runBlocking {
            val r1 = flow.invoke("1")
            val r2 = flow.invoke("2")
            r2
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Passed.Succeeded)
        Assert.assertEquals(result.code, Codes.SUCCESS.code)
        Assert.assertEquals(2, result.getOrElse { -1 })
        Assert.assertEquals(2, calls.totalRuns())
        Assert.assertEquals(2, counts.totalProcessed())
        Assert.assertEquals(2, everyValue)
    }

}