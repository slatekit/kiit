package test.core

import org.junit.Assert
import org.junit.Test
//import slatekit.core.gate.*

/*
class GateTests {

    fun settings():GateSettings = GateSettings(
            5,
            .20,
            0,
            0, 3,
            false)

    @Test
    fun can_setup() {
        val gate = Gate("sample", settings(), { sender, event ->
            println()
        })
        val metrics = gate.metrics()
        Assert.assertEquals( metrics.state, Open)
        Assert.assertEquals( metrics.processedCount, 0)
        Assert.assertEquals( metrics.processedTotal, 0)
        Assert.assertEquals( metrics.currentBatch, 0)
        Assert.assertTrue(metrics.errorLast == null)
        Assert.assertTrue(metrics.errorCount == 0)
        Assert.assertTrue(metrics.errorTotal == 0L)
    }


    @Test
    fun can_track_processed() {
        val gate = Gate("sample", settings(), { sender, event ->
            println()
        })
        gate.attempt { 1 }
        gate.attempt { 2 }
        gate.attempt { 3 }
        gate.attempt { 4 }
        gate.attempt { 5 }
        gate.attempt { 6 }
        val metrics = gate.metrics()
        Assert.assertEquals( metrics.state, Open)
        Assert.assertEquals( metrics.processedCount, 0)
        Assert.assertEquals( metrics.processedTotal, 6)
        Assert.assertEquals( metrics.currentBatch, 2)
        Assert.assertTrue(metrics.errorLast == null)
        Assert.assertTrue(metrics.errorCount == 0)
        Assert.assertTrue(metrics.errorTotal == 0L)
    }


    @Test
    fun can_track_errors() {
        val settings = GateSettings(
                0,
                .20,
                0,
                0, 6,
                false)
        val gate = Gate("sample", settings, { _, event ->
            println("gate ${event.name} event : " + event.reason)
        })
        (1..12).forEach{ ndx -> gate.attempt {
            if(ndx % 6 == 0)
                throw Exception("test $ndx")
            else
                ndx
        } }
        val metrics = gate.metrics()
        Assert.assertEquals( metrics.state, Open)
        Assert.assertEquals( metrics.processedCount, 0)
        Assert.assertEquals( metrics.processedTotal, 12)
        Assert.assertEquals( metrics.currentBatch, 2)
        Assert.assertTrue(metrics.errorLast?.message == "test 12")
        Assert.assertTrue(metrics.errorCount == 0)
        Assert.assertTrue(metrics.errorTotal == 2L)
    }


    @Test
    fun can_close() {
        val gate = Gate("sample", settings(), { _, event ->
            println("gate ${event.name} event : " + event.reason)
        })
        (1..20).forEach{ ndx -> gate.attempt {
            if(ndx == 12) throw Exception("test")
            else ndx
        } }
        val metrics = gate.metrics()
        Assert.assertEquals( metrics.state, Closed)
        Assert.assertEquals( metrics.reason, ErrorsHigh)
        Assert.assertEquals( metrics.processedTotal, 12)
        Assert.assertEquals( metrics.currentBatch, 4)
        Assert.assertTrue(metrics.errorLast?.message == "test")
        Assert.assertTrue(metrics.errorCount == 0)
        Assert.assertTrue(metrics.errorTotal == 1L)
    }


    @Test
    fun can_alert() {
        var alerted = false
        val gate = Gate("sample", settings(), { _, event ->
            println("gate ${event.name} event : " + event.reason)
            alerted = true
        })
        (1..20).forEach{ ndx -> gate.attempt {
            if(ndx == 12) throw Exception("test")
            else ndx
        } }
        val metrics = gate.metrics()
        Assert.assertTrue(alerted)
        Assert.assertEquals( metrics.state, Closed)
        Assert.assertEquals( metrics.reason, ErrorsHigh)
        Assert.assertEquals( metrics.processedTotal, 12)
        Assert.assertEquals( metrics.currentBatch, 4)
        Assert.assertTrue(metrics.errorLast?.message == "test")
        Assert.assertTrue(metrics.errorCount == 0)
        Assert.assertTrue(metrics.errorTotal == 1L)
    }


    @Test
    fun can_reopen() {
        var alerted = false
        val gate = Gate("sample", settings(), { _, event ->
            println("gate ${event.name} event : " + event.reason)
            alerted = true
        })
        (1..20).forEach{ ndx -> gate.attempt {
            if(ndx == 12) throw Exception("test")
            else ndx
        } }
        val metrics = gate.metrics()
        Assert.assertTrue(alerted)
        Assert.assertEquals( metrics.state, Closed)
        Assert.assertEquals( metrics.reason, ErrorsHigh)
        Assert.assertEquals( metrics.processedTotal, 12)
        Assert.assertEquals( metrics.currentBatch, 4)
        Assert.assertTrue(metrics.errorLast?.message == "test")
        Assert.assertTrue(metrics.errorCount == 0)
        Assert.assertTrue(metrics.errorTotal == 1L)

        gate.openLater(1,false)
        Thread.sleep(1200)
        val metrics2 = gate.metrics()
        Assert.assertEquals(metrics2.state, Open)
        Assert.assertEquals(metrics2.reason, NotApplicable)
    }
}

*/