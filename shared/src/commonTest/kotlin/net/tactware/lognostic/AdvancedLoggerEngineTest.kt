package net.tactware.lognostic

import net.tactware.lognostic.frequency.RateLimitingFrequencyController
import net.tactware.lognostic.impl.AdvancedLoggerEngine
import net.tactware.lognostic.impl.AdvancedLoggerEngineBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdvancedLoggerEngineTest {

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
    fun messageIsForwardedToDelegateWithTagAndImportancePrefix() {
        val (delegate, records) = recordingLogger()
        val engine = AdvancedLoggerEngine(delegates = listOf(delegate))
        engine.log(LogLevel.INFO, "MyTag", LogImportance.HIGH, "hello")
        assertEquals(1, records.size)
        assertTrue(records[0].second.contains("MyTag"))
        assertTrue(records[0].second.contains("HIGH"))
        assertTrue(records[0].second.contains("hello"))
    }

    @Test
    fun messageWithoutTagIsFormattedWithoutTagPrefix() {
        val (delegate, records) = recordingLogger()
        val engine = AdvancedLoggerEngine(delegates = listOf(delegate))
        engine.log(LogLevel.DEBUG, null, LogImportance.LOW, "no tag")
        assertEquals(1, records.size)
        assertFalse(records[0].second.contains("[null]"))
        assertTrue(records[0].second.contains("no tag"))
    }

    @Test
    fun globalMinLevelFiltersOutLowerLevelMessages() {
        val (delegate, records) = recordingLogger()
        val engine = AdvancedLoggerEngine(
            delegates = listOf(delegate),
            globalMinLevel = LogLevel.WARN
        )
        engine.log(LogLevel.DEBUG, null, LogImportance.HIGH, "debug msg")
        engine.log(LogLevel.WARN, null, LogImportance.HIGH, "warn msg")
        assertEquals(1, records.size)
        assertTrue(records[0].second.contains("warn msg"))
    }

    @Test
    fun globalMinImportanceFiltersOutLowerImportanceMessages() {
        val (delegate, records) = recordingLogger()
        val engine = AdvancedLoggerEngine(
            delegates = listOf(delegate),
            globalMinImportance = LogImportance.HIGH
        )
        engine.log(LogLevel.ERROR, null, LogImportance.LOW, "low importance")
        engine.log(LogLevel.ERROR, null, LogImportance.HIGH, "high importance")
        assertEquals(1, records.size)
        assertTrue(records[0].second.contains("high importance"))
    }

    @Test
    fun tagFilterOverridesGlobalDefaultsForMatchingTags() {
        val (delegate, records) = recordingLogger()
        val filter = TagFilter("network*", minLevel = LogLevel.ERROR, minImportance = LogImportance.CRITICAL)
        val engine = AdvancedLoggerEngine(
            delegates = listOf(delegate),
            tagFilters = listOf(filter),
            globalMinLevel = LogLevel.VERBOSE
        )
        engine.log(LogLevel.DEBUG, "networkHttp", LogImportance.CRITICAL, "debug net")
        engine.log(LogLevel.ERROR, "networkHttp", LogImportance.CRITICAL, "error net")
        assertEquals(1, records.size)
        assertTrue(records[0].second.contains("error net"))
    }

    @Test
    fun frequencyControllerSuppressesExcessMessages() {
        val (delegate, records) = recordingLogger()
        val engine = AdvancedLoggerEngine(
            delegates = listOf(delegate),
            frequencyController = RateLimitingFrequencyController(maxLogsPerWindow = 2, windowMillis = 10_000L)
        )
        engine.log(LogLevel.INFO, "tag", LogImportance.MEDIUM, "msg")
        engine.log(LogLevel.INFO, "tag", LogImportance.MEDIUM, "msg")
        engine.log(LogLevel.INFO, "tag", LogImportance.MEDIUM, "msg")
        assertEquals(2, records.size)
    }

    @Test
    fun basicLogMethodRoutesThroughAdvancedLogWithDefaults() {
        val (delegate, records) = recordingLogger()
        val engine = AdvancedLoggerEngine(delegates = listOf(delegate))
        engine.log(LogLevel.INFO, "basic message")
        assertEquals(1, records.size)
        assertTrue(records[0].second.contains("basic message"))
    }

    @Test
    fun builderThrowsWhenNoDelegatesAreAdded() {
        assertFailsWith<IllegalStateException> {
            AdvancedLoggerEngineBuilder().build()
        }
    }

    @Test
    fun builderProducesFunctionalEngine() {
        val (delegate, records) = recordingLogger()
        val engine = AdvancedLoggerEngineBuilder()
            .addDelegate(delegate)
            .setGlobalMinLevel(LogLevel.INFO)
            .setGlobalMinImportance(LogImportance.MEDIUM)
            .build()
        engine.log(LogLevel.DEBUG, "tag", LogImportance.HIGH, "filtered out")
        engine.log(LogLevel.INFO, "tag", LogImportance.MEDIUM, "allowed")
        assertEquals(1, records.size)
        assertTrue(records[0].second.contains("allowed"))
    }
}
