package se.tp21

import se.tp21.Result.Failure
import se.tp21.Result.Success

fun <Er: Error, Ev: Event> Monitor<Ev>.notifyFailure(errorToEvent: (Er) -> Ev) = NotifyFailure(this, errorToEvent)

class NotifyFailure<Er : Error, Ev : Event>(
    val monitor: Monitor<Ev>,
    private val errorToEvent: (Er) -> Ev
) {
    operator fun <T> invoke(result: NotifyFailure<Er, Ev>.() -> Result<Er, T>): Result<Er, T> =
        result().let { result ->
            when (result) {
                is Failure -> result.also { monitor.notify(errorToEvent(it.error)) }
                is Success -> result
            }
        }
}