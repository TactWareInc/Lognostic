package net.tactware.lognostic.frequency

import net.tactware.lognostic.currentTimeMillis

/**
 * A [FrequencyController] that enforces a maximum number of log messages per tag
 * within a rolling time window.
 *
 * @param maxLogsPerWindow The maximum number of logs allowed per tag within the window.
 * @param windowMillis The duration of the rolling time window in milliseconds.
 */
class RateLimitingFrequencyController(
    private val maxLogsPerWindow: Int = 10,
    private val windowMillis: Long = 1_000L
) : FrequencyController {

    private val lock = Any()
    private val tagTimestamps = mutableMapOf<String, ArrayDeque<Long>>()

    override fun shouldLog(tag: String?, message: String): Boolean {
        val key = tag ?: "__untagged__"
        val now = currentTimeMillis()

        synchronized(lock) {
            val timestamps = tagTimestamps.getOrPut(key) { ArrayDeque() }
            evictExpired(timestamps, now)
            if (timestamps.size >= maxLogsPerWindow) return false
            timestamps.addLast(now)
        }
        return true
    }

    private fun evictExpired(timestamps: ArrayDeque<Long>, now: Long) {
        while (timestamps.isNotEmpty() && now - timestamps.first() > windowMillis) {
            timestamps.removeFirst()
        }
    }

    /**
     * Clears all recorded timestamps, resetting rate limit state for all tags.
     */
    fun reset() {
        synchronized(lock) {
            tagTimestamps.clear()
        }
    }
}
