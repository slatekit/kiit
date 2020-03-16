package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.log.Log
import slatekit.common.log.LogEntry
import slatekit.common.log.LogLevel
import slatekit.common.log.Logger
import slatekit.common.naming.*


class LogTests {

    fun ensure(namer: Namer, input:String, expected:String){
        Assert.assertTrue(namer.rename(input) == expected)
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


    @Test fun can_ensure_static() {
        val logger = MemoryLogger(LogLevel.Info)
        Log.init(logger)
        Log.debug("d")
        Log.info("i")
        Log.warn("w")
        Log.error("e")
        Log.fatal("f")
        Assert.assertEquals(4, logger.entries.size)
        Assert.assertEquals("i", logger.entries[0].msg)
        Assert.assertEquals("w", logger.entries[1].msg)
        Assert.assertEquals("e", logger.entries[2].msg)
        Assert.assertEquals("f", logger.entries[3].msg)
    }


    class MemoryLogger(level:LogLevel) : Logger(level) {

        var entries = mutableListOf<LogEntry>()

        override fun log(entry: LogEntry) {
            entries.add(entry)
        }
    }
}
