package se.tp21.resultmonitor

sealed class TestError : Error
data class AnError(val message: String = "AnError") : TestError()

sealed class TestEvent : Event
data class StartEvent(val message: String = "StartEvent") : TestEvent()
data class ErrorEvent(val message: String = "ErrorEvent") : TestEvent()

class TestMonitor : Monitor<TestEvent> {
    val events = mutableListOf<TestEvent>()
    override fun notify(event: TestEvent) {
        events.add(event)
    }
}

