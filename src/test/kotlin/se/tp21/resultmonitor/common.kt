package se.tp21.resultmonitor

import se.tp21.Error
import se.tp21.Event
import se.tp21.Monitor

sealed class TestError(override val message: String) : Error {
    class AnError : TestError("error")
}

sealed class TestEvent(override val message: String) : Event {
    class StartEvent : TestEvent("start")
    class ErrorEvent(override val message: String) : TestEvent("error")
}

class TestMonitor : Monitor<TestEvent> {
    val events = mutableListOf<TestEvent>()
    override fun notify(event: TestEvent) {
        events.add(event)
    }
}

