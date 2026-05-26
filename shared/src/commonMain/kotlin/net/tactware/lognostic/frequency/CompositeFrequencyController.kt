package net.tactware.lognostic.frequency

/**
 * A [FrequencyController] that chains multiple controllers together.
 * A log message is only allowed if all delegate controllers permit it.
 *
 * @param controllers The list of controllers to evaluate in order.
 */
class CompositeFrequencyController(private val controllers: List<FrequencyController>) : FrequencyController {

    constructor(vararg controllers: FrequencyController) : this(controllers.toList())

    override fun shouldLog(tag: String?, message: String): Boolean {
        return controllers.all { it.shouldLog(tag, message) }
    }
}
