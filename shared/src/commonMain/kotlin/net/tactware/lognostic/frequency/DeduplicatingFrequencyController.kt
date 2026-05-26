package net.tactware.lognostic.frequency

import net.tactware.lognostic.currentTimeMillis

/**
 * A [FrequencyController] that suppresses repeated identical messages within a time window.
 * A message is considered a duplicate if the same (tag + message) combination was already
 * logged within the configured window.
 *
 * @param windowMillis The duration in milliseconds during which duplicate messages are suppressed.
 */
class DeduplicatingFrequencyController(
    private val windowMillis: Long = 5_000L
) : FrequencyController {

    private val lock = Any()
    private val lastSeen = mutableMapOf<String, Long>()

    override fun shouldLog(tag: String?, message: String): Boolean {
        val key = "${tag ?: "__untagged__"}::$message"
        val now = currentTimeMillis()

        synchronized(lock) {
            val previous = lastSeen[key]
            if (previous != null && now - previous < windowMillis) return false
            lastSeen[key] = now
        }
        return true
    }

    /**
     * Clears all recorded message timestamps, resetting deduplication state.
     */
    fun reset() {
        synchronized(lock) {
            lastSeen.clear()
        }
    }
}
