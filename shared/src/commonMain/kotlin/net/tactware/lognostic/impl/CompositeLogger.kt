package net.tactware.lognostic.impl

import net.tactware.lognostic.LogLevel
import net.tactware.lognostic.Logger

/**
 * A [Logger] implementation that fans out each log message to multiple delegate loggers.
 * All delegates receive every message; individual filtering is the responsibility of each delegate.
 *
 * @param delegates The list of loggers to which all messages are forwarded.
 */
class CompositeLogger(private val delegates: List<Logger>) : Logger {

    constructor(vararg delegates: Logger) : this(delegates.toList())

    override fun log(level: LogLevel, message: String, throwable: Throwable?) {
        delegates.forEach { it.log(level, message, throwable) }
    }
}
