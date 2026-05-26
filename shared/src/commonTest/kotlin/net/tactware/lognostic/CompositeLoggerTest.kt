package net.tactware.lognostic

import net.tactware.lognostic.impl.CompositeLogger
import kotlin.test.Test
import kotlin.test.assertEquals

class CompositeLoggerTest {

    private fun recordingLogger(): Pair<Logger, MutableList<Pair<LogLevel, String>>> {
        val records = mutableListOf<Pair<LogLevel, String>>()
        val logger = object : Logger {
            override fun log(level: LogLevel, message: String, throwable: Throwable?) {
                records.add(level to message)
            }
        }
        return logger to records
    }

    @Test
    fun logIsForwardedToAllDelegates() {
        val (l1, r1) = recordingLogger()
        val (l2, r2) = recordingLogger()
        val composite = CompositeLogger(l1, l2)
        composite.log(LogLevel.INFO, "hello")
        assertEquals(1, r1.size)
        assertEquals(1, r2.size)
        assertEquals(LogLevel.INFO to "hello", r1[0])
        assertEquals(LogLevel.INFO to "hello", r2[0])
    }

    @Test
    fun allLogLevelsAreForwardedToAllDelegates() {
        val (l1, r1) = recordingLogger()
        val composite = CompositeLogger(l1)
        LogLevel.entries.forEach { level -> composite.log(level, "msg") }
        assertEquals(LogLevel.entries.size, r1.size)
    }
}
