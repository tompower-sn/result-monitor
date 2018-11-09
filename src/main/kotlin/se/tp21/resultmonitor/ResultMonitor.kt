package se.tp21

import se.tp21.Result.Failure
import se.tp21.Result.Success

class ResultMonitor<Er : Error, Ev : MonitoringEvent, T>(
    private val monitor: Monitor<Ev>,
    private val errorEventMap: Map<Er, Ev> = mapOf()
) : (() -> Result<Er, T>) -> Result<Er, T> {
    override fun invoke(result: () -> Result<Er, T>): Result<Er, T> =
        result().run {
            when (this) {
                is Failure -> apply { monitor.notify(value.toEvent()) }
                is Success -> this
            }
        }

    private fun Error.toEvent(): Ev = errorEventMap[this] ?: this as Ev
}