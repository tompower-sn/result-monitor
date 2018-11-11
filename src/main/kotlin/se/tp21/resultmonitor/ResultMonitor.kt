package se.tp21

import se.tp21.Result.Failure
import se.tp21.Result.Success

class ResultMonitor<Ev : Event>(
    private val monitor: Monitor<Ev>
) {
    fun <Er : Error> notifyFailure(errorToEvent: (Er) -> Ev): NotifyFailure<Er, Ev> = NotifyFailure(monitor, errorToEvent)
}

interface NotifyResult<Er : Error, Ev : Event> {
    val monitor: Monitor<Ev>
    operator fun <T> invoke(result: NotifyResult<Er, Ev>.() -> Result<Er, T>): Result<Er, T>
}

class NotifyFailure<Er : Error, Ev : Event>(
    override val monitor: Monitor<Ev>,
    private val errorToEvent: (Er) -> Ev
) : NotifyResult<Er, Ev> {
    override operator fun <T> invoke(result: NotifyResult<Er, Ev>.() -> Result<Er, T>): Result<Er, T> =
        result().let {
            when (it) {
                is Failure -> it.also { monitor.notify(errorToEvent(it.error)) }
                is Success -> it
            }
        }
}