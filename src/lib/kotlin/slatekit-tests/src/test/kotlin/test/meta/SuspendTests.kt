package test.meta

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.apis.services.Calls

class SuspendTests {

    @Test
    fun callSuspend(){
        val sample = ReflectSample()
        runBlocking {
            val result = Calls.callMethod(ReflectSample::class, sample, "add", arrayOf(1, 2))
            Assert.assertEquals(3, result)
        }
    }


    @Test
    fun callNormal(){
        val sample = ReflectSample()
        runBlocking {
            val result = Calls.callMethod(ReflectSample::class, sample, "inc", arrayOf(1, 2))
            Assert.assertEquals(3, result)
        }
    }
}


class ReflectSample {

    suspend fun add(a: Int, b:Int):Int {
        val result = a + b
        return result
    }


    fun inc(a: Int, b:Int):Int {
        val result = a + b
        return result
    }
}