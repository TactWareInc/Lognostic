package net.tactware.lognostic.frequency

/**
 * A [FrequencyController] that logs only 1 in every N occurrences of a given (tag + message) pair.
 *
 * @param sampleRate The sampling rate. A value of N means 1 out of every N messages is logged.
 */
class SamplingFrequencyController(private val sampleRate: Int = 10) : FrequencyController {

    init {
        require(sampleRate >= 1) { "sampleRate must be at least 1" }
    }

    private val lock = Any()
    private val counters = mutableMapOf<String, Long>()

    override fun shouldLog(tag: String?, message: String): Boolean {
        val key = "${tag ?: "__untagged__"}::$message"
        val count = synchronized(lock) {
            val next = counters.getOrDefault(key, 0L) + 1L
            counters[key] = next
            next
        }
        return count % sampleRate == 0L
    }

    /**
     * Resets all counters, restarting the sampling cycle for all messages.
     */
    fun reset() {
        synchronized(lock) {
            counters.clear()
        }
    }
}
