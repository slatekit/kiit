/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */
package test

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Emitter


class Emitter_Tests {

    @Test
    fun can_init() {
        val emitter = Emitter<String>()
        Assert.assertTrue(emitter.listeners.isEmpty())
    }

    @Test
    fun can_subscribe_on() {
        perform<String> { emitter ->
            var name = ""
            emitter.on("test") { s -> name = s}
            emitter.emit("test", "sk")
            val listeners = emitter.listeners
            Assert.assertEquals("test", listeners[0].name)
            Assert.assertEquals(null, listeners[0].limit)
            Assert.assertEquals("sk", name)
        }
    }

    @Test
    fun can_subscribe_one() {
        perform<String> { emitter ->
            var value = ""
            emitter.one("test") { s -> value = s}
            val listeners1 = emitter.listeners
            Assert.assertEquals(1, listeners1.size)
            Assert.assertEquals("test", listeners1[0].name)
            Assert.assertEquals(1, listeners1[0].limit)

            // Emit
            emitter.emit("test", "1")
            emitter.emit("test", "2")

            val listeners2 = emitter.listeners
            Assert.assertEquals(0, listeners2.size)
            Assert.assertEquals("1", value)
        }
    }

    @Test
    fun can_subscribe_max() {
        perform<String> { emitter ->
            var value = ""
            emitter.max("test", 2) { s -> value = s}
            val listeners1 = emitter.listeners
            Assert.assertEquals(1, listeners1.size)
            Assert.assertEquals("test", listeners1[0].name)
            Assert.assertEquals(2, listeners1[0].limit)

            // Emit
            emitter.emit("test", "1")
            emitter.emit("test", "2")
            emitter.emit("test", "3")

            val listeners2 = emitter.listeners
            Assert.assertEquals(0, listeners2.size)
            Assert.assertEquals("2", value)
        }
    }

    @Test
    fun can_subscribe_multiple_with_max() {
        perform<String> { emitter ->
            var value1 = ""
            var value2 = ""
            var value = ""
            emitter.on("test") { s -> value = s}
            emitter.one("test") { s -> value1 = s}
            emitter.max( "test", 2)  { s -> value2 = s }

            val listeners1 = emitter.listeners
            Assert.assertEquals(3, listeners1.size)
            Assert.assertEquals("test", listeners1[0].name)
            Assert.assertEquals("test", listeners1[1].name)
            Assert.assertEquals("test", listeners1[2].name)
            Assert.assertEquals(null, listeners1[0].limit)
            Assert.assertEquals(1, listeners1[1].limit)
            Assert.assertEquals(2, listeners1[2].limit)

            // Emit
            emitter.emit("test", "1")
            emitter.emit("test", "2")
            emitter.emit("test", "3")

            val listeners2 = emitter.listeners
            Assert.assertEquals(1, listeners2.size)
            Assert.assertEquals(null, listeners2[0].limit)
            Assert.assertEquals("1", value1)
            Assert.assertEquals("2", value2)
            Assert.assertEquals("3", value)
        }
    }


    private fun <T> perform(op:suspend (Emitter<T>) -> Unit) {
        val emitter = Emitter<T>()
        runBlocking {
            op(emitter)
        }
    }
}
