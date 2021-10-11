package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.log.LogEntry
import slatekit.common.log.LogLevel
import slatekit.common.log.Logger
import slatekit.utils.naming.*
import slatekit.common.newline


class LogTests {

    fun ensure(namer: Namer, input:String, expected:String){
        Assert.assertTrue(namer.rename(input) == expected)
    }

    @Test fun can_build_message_with_args(){
        val template = "error on action=%s, id=%s, name=%s"
        val expected = template.format("register", "user01", "batman")
        val logger = MemoryLogger(LogLevel.Info)
        logger.info(template, "register", "user01", "batman")
        val actual = logger.entries.first().msg
        Assert.assertEquals(expected, actual)
    }


    @Test fun can_build_message_with_exception(){
        val ex = Exception("testing exception message")
        val logger = MemoryLogger(LogLevel.Info)
        logger.info(ex, "error check")
        val actual = logger.entries.first().msg
        val expected = "error check" + newline + "testing exception message"
        Assert.assertEquals(expected, actual)
    }


    @Test fun can_build_message_with_exception_only(){
        val ex = Exception("testing exception message")
        val logger = MemoryLogger(LogLevel.Info)
        logger.info(ex)
        val actual = logger.entries.first().msg
        val expected = "testing exception message"
        Assert.assertEquals(expected, actual)
    }


    @Test fun can_build_message_with_key_value_pairs(){
        val info = listOf("a" to 1, "b" to true, "email" to "user1@gmail.com", "c" to "kotlin", "phone" to "123-456-7890", "d" to 2.3)
        val logger = MemoryLogger(LogLevel.Info)
        logger.info("pairs", info)
        val actual = logger.entries.first().msg
        val expected = "pairs : a=1, b=true, c=kotlin, d=2.3"
        Assert.assertEquals(expected, actual)
    }


    @Test fun can_ensure_level() {
        fun test(level: LogLevel) {
            val logger = MemoryLogger(level)
            logger.debug("d")
            logger.info("i")
            logger.warn("w")
            logger.error("e")
            logger.fatal("f")

            // Check
            val min = LogLevel.Debug.code
            val max = LogLevel.Fatal.code
            val entries = logger.entries
            val expectedCount = (max - level.code) + 1
            Assert.assertEquals(expectedCount, entries.size)
            entries.fold(level.code) { acc, entry ->
                Assert.assertEquals(acc, entry.level.code)
                acc + 1
            }
        }

        val levels = listOf(LogLevel.Debug, LogLevel.Info, LogLevel.Warn, LogLevel.Error, LogLevel.Fatal)
        levels.forEach {
            test(it)
        }
    }


    class MemoryLogger(level:LogLevel) : Logger(level) {

        var entries = mutableListOf<LogEntry>()

        override fun log(entry: LogEntry) {
            entries.add(entry)
        }
    }
}
