package se.tp21

import se.tp21.Result.Failure
import se.tp21.Result.Success

class ResultMonitor<Er : Error, Ev : Event>(
    val monitor: Monitor<Ev>,
    private val toEvent: Er.() -> Ev = { this as Ev }
) {
    operator fun <T> invoke(result: ResultMonitor<Er, Ev>.() -> Result<Er, T>): Result<Er, T> =
        result().run {
            when (this) {
                is Failure -> also { monitor.notify(value.toEvent()) }
                is Success -> this
            }
        }
}