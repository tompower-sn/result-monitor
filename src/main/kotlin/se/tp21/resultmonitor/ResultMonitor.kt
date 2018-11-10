package se.tp21

import se.tp21.Result.Failure
import se.tp21.Result.Success

class ResultMonitor<Er : Error, Ev : Event>(
    private val monitor: Monitor<Ev>,
    private val errorToEvent: (Er) -> Ev = { error: Er -> error as Ev }
) {
    operator fun <T> invoke(result: () -> Result<Er, T>): Result<Er, T> =
        result().run {
            when (this) {
                is Failure -> also { monitor.notify(errorToEvent(value)) }
                is Success -> this
            }
        }
}