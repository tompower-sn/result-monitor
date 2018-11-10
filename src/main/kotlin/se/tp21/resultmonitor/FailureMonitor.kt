package se.tp21

import se.tp21.Result.Failure
import se.tp21.Result.Success

class FailureMonitor<Er : Error, Ev : Event>(
    val monitor: Monitor<Ev>,
    private val toEvent: Er.() -> Ev
) {
    operator fun <T> invoke(result: FailureMonitor<Er, Ev>.() -> Result<Er, T>): Result<Er, T> =
        result().let {
            when (it) {
                is Failure -> it.also { monitor.notify(it.error.toEvent()) }
                is Success -> it
            }
        }
}