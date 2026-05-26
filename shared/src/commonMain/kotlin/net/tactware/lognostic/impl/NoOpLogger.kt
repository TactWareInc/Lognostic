package net.tactware.lognostic.impl

import net.tactware.lognostic.LogLevel
import net.tactware.lognostic.Logger

/**
 * A [Logger] implementation that silently discards all log messages.
 * Useful as a default no-op logger in libraries or during testing.
 */
class NoOpLogger : Logger {
    override fun log(level: LogLevel, message: String, throwable: Throwable?) = Unit
}
