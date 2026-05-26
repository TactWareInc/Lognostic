package net.tactware.lognostic

import net.tactware.lognostic.frequency.CompositeFrequencyController
import net.tactware.lognostic.frequency.DeduplicatingFrequencyController
import net.tactware.lognostic.frequency.RateLimitingFrequencyController
import net.tactware.lognostic.frequency.SamplingFrequencyController
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FrequencyControllerTest {

    @Test
    fun rateLimiterAllowsMessagesUpToTheLimit() {
        val controller = RateLimitingFrequencyController(maxLogsPerWindow = 3, windowMillis = 10_000L)
        assertTrue(controller.shouldLog("tag", "msg"))
        assertTrue(controller.shouldLog("tag", "msg"))
        assertTrue(controller.shouldLog("tag", "msg"))
        assertFalse(controller.shouldLog("tag", "msg"))
    }

    @Test
    fun rateLimiterTracksTagsIndependently() {
        val controller = RateLimitingFrequencyController(maxLogsPerWindow = 1, windowMillis = 10_000L)
        assertTrue(controller.shouldLog("tagA", "msg"))
        assertFalse(controller.shouldLog("tagA", "msg"))
        assertTrue(controller.shouldLog("tagB", "msg"))
    }

    @Test
    fun rateLimiterResetClearsAllState() {
        val controller = RateLimitingFrequencyController(maxLogsPerWindow = 1, windowMillis = 10_000L)
        controller.shouldLog("tag", "msg")
        controller.reset()
        assertTrue(controller.shouldLog("tag", "msg"))
    }

    @Test
    fun deduplicatorSuppressesIdenticalMessagesWithinWindow() {
        val controller = DeduplicatingFrequencyController(windowMillis = 10_000L)
        assertTrue(controller.shouldLog("tag", "msg"))
        assertFalse(controller.shouldLog("tag", "msg"))
    }

    @Test
    fun deduplicatorAllowsSameMessageWithDifferentTags() {
        val controller = DeduplicatingFrequencyController(windowMillis = 10_000L)
        assertTrue(controller.shouldLog("tagA", "msg"))
        assertTrue(controller.shouldLog("tagB", "msg"))
    }

    @Test
    fun deduplicatorAllowsDifferentMessagesWithSameTag() {
        val controller = DeduplicatingFrequencyController(windowMillis = 10_000L)
        assertTrue(controller.shouldLog("tag", "msg1"))
        assertTrue(controller.shouldLog("tag", "msg2"))
    }

    @Test
    fun deduplicatorResetClearsAllState() {
        val controller = DeduplicatingFrequencyController(windowMillis = 10_000L)
        controller.shouldLog("tag", "msg")
        controller.reset()
        assertTrue(controller.shouldLog("tag", "msg"))
    }

    @Test
    fun samplerLogsOnlyEveryNthOccurrence() {
        val controller = SamplingFrequencyController(sampleRate = 3)
        assertFalse(controller.shouldLog("tag", "msg"))
        assertFalse(controller.shouldLog("tag", "msg"))
        assertTrue(controller.shouldLog("tag", "msg"))
        assertFalse(controller.shouldLog("tag", "msg"))
    }

    @Test
    fun samplerWithRate1AllowsAllMessages() {
        val controller = SamplingFrequencyController(sampleRate = 1)
        repeat(5) { assertTrue(controller.shouldLog("tag", "msg")) }
    }

    @Test
    fun samplerResetRestartsTheCycle() {
        val controller = SamplingFrequencyController(sampleRate = 2)
        controller.shouldLog("tag", "msg")
        assertTrue(controller.shouldLog("tag", "msg"))
        controller.reset()
        assertFalse(controller.shouldLog("tag", "msg"))
    }

    @Test
    fun compositeControllerRequiresAllDelegatesToAllow() {
        val allowAll = SamplingFrequencyController(sampleRate = 1)
        val denyAll = RateLimitingFrequencyController(maxLogsPerWindow = 0, windowMillis = 10_000L)
        val composite = CompositeFrequencyController(allowAll, denyAll)
        assertFalse(composite.shouldLog("tag", "msg"))
    }

    @Test
    fun compositeControllerAllowsWhenAllDelegatesAllow() {
        val c1 = SamplingFrequencyController(sampleRate = 1)
        val c2 = DeduplicatingFrequencyController(windowMillis = 10_000L)
        val composite = CompositeFrequencyController(c1, c2)
        assertTrue(composite.shouldLog("tag", "unique_msg_composite"))
    }
}
