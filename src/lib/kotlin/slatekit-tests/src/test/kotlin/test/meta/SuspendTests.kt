package test.meta

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.apis.executor.Executor
import kiit.meta.Reflector

class SuspendTests {

    @Test
    fun callSuspend(){
        val sample = ReflectSample()
        runBlocking {
            val member = Reflector.getMethod(ReflectSample::class, "add")
            val result = Executor.invoke(sample, member!!, arrayOf(1, 2))
            Assert.assertEquals(3, result)
        }
    }


    @Test
    fun callNormal(){
        val sample = ReflectSample()
        runBlocking {
            val member = Reflector.getMethod(ReflectSample::class, "inc")
            val result = Executor.invoke(sample, member!!, arrayOf(1, 2))
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