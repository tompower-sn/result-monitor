package se.tp21

import se.tp21.Result.Failure
import se.tp21.Result.Success

fun <Er : Error, Ev: Event> Monitor<Ev>.notifyFailure(failureToEvent: (Failure<Er>) -> Ev) = NotifyFailure(this, failureToEvent)

class NotifyFailure<Er : Error, Ev: Event>(
    val monitor: Monitor<Ev>,
    private val failureToEvent: (Failure<Er>) -> Ev
) {
    operator fun <T> invoke(block: NotifyFailure<Er, Ev>.() -> Result<Er, T>): Result<Er, T> =
        block().let { result ->
            when (result) {
                is Failure -> result.also { monitor.notify(failureToEvent(it)) }
                is Success -> result
            }
        }
}