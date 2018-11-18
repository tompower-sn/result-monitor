package se.tp21.resultmonitor

fun <T : Any, Er : Error, Ev : Event> Monitor<Ev>.notifyFailure(
    failureEvent: (Failure<Er>) -> Ev?
): NotifyResult<T, Er, Ev> =
    notifyResult {
        when (it) {
            is Success -> null
            is Failure -> failureEvent(it)
        }
    }

fun <T : Any, Er : Error, Ev : Event> Monitor<Ev>.notifyResult(
    resultEvent: (Result<Er, T>) -> Ev?
) = NotifyResult(this, resultEvent)

class NotifyResult<T : Any, Er : Error, Ev : Event>(
    val monitor: Monitor<Ev>,
    private val resultEvent: (Result<Er, T>) -> Ev?
) {
    operator fun invoke(
        block: NotifyResult<T, Er, Ev>.() -> Result<Er, T>
    ): Result<Er, T> =
        block().also {
            resultEvent(it)?.let { monitor.notify(it) }
        }
}