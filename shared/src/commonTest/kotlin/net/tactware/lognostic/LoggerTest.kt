package net.tactware.lognostic

import net.tactware.lognostic.impl.NoOpLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggerTest {

    @Test
    fun noOpLoggerDoesNotThrowOnAnyLogLevel() {
        val logger: Logger = NoOpLogger()
        LogLevel.entries.forEach { level ->
            logger.log(level, "test message")
        }
    }

    @Test
    fun loggerConvenienceMethodsDelegateToLog() {
        val recorded = mutableListOf<LogLevel>()
        val logger = object : Logger {
            override fun log(level: LogLevel, message: String, throwable: Throwable?) {
                recorded.add(level)
            }
        }
        logger.v("v")
        logger.d("d")
        logger.i("i")
        logger.w("w")
        logger.e("e")
        logger.wtf("f")
        assertEquals(
            listOf(LogLevel.VERBOSE, LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR, LogLevel.FATAL),
            recorded
        )
    }

    @Test
    fun logLevelPrioritiesAreOrderedCorrectly() {
        assertTrue(LogLevel.VERBOSE.priority < LogLevel.DEBUG.priority)
        assertTrue(LogLevel.DEBUG.priority < LogLevel.INFO.priority)
        assertTrue(LogLevel.INFO.priority < LogLevel.WARN.priority)
        assertTrue(LogLevel.WARN.priority < LogLevel.ERROR.priority)
        assertTrue(LogLevel.ERROR.priority < LogLevel.FATAL.priority)
    }

    @Test
    fun logImportanceWeightsAreOrderedCorrectly() {
        assertTrue(LogImportance.LOW.weight < LogImportance.MEDIUM.weight)
        assertTrue(LogImportance.MEDIUM.weight < LogImportance.HIGH.weight)
        assertTrue(LogImportance.HIGH.weight < LogImportance.CRITICAL.weight)
    }

    @Test
    fun logEntryStoresAllFieldsCorrectly() {
        val throwable = RuntimeException("oops")
        val entry = LogEntry(LogLevel.ERROR, "MyTag", LogImportance.HIGH, "Something failed", throwable)
        assertEquals(LogLevel.ERROR, entry.level)
        assertEquals("MyTag", entry.tag)
        assertEquals(LogImportance.HIGH, entry.importance)
        assertEquals("Something failed", entry.message)
        assertEquals(throwable, entry.throwable)
    }
}
