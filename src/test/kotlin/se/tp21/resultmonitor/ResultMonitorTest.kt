package se.tp21.resultmonitor

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import se.tp21.*
import se.tp21.Result.Failure
import se.tp21.Result.Success
import se.tp21.resultmonitor.ResultMonitorTest.TestError.AnError
import se.tp21.resultmonitor.ResultMonitorTest.TestEvent.ErrorEvent
import se.tp21.resultmonitor.ResultMonitorTest.TestEvent.StartEvent

class ResultMonitorTest {

    sealed class TestError(override val message: String) : Error {
        class AnError : TestError("error")
    }

    sealed class TestEvent(override val message: String) : Event {
        class StartEvent : TestEvent("start")
        class ErrorEvent : TestEvent("error")
    }

    private fun TestError.toEvent(): TestEvent =
        when (this) {
            is AnError -> ErrorEvent()
        }

    class TestMonitor : Monitor<TestEvent> {
        val events: MutableList<TestEvent> = mutableListOf()
        override fun notify(event: TestEvent) {
            events.add(event)
        }
    }

    class Monitored(
        private val resultMonitor: ResultMonitor<TestError, TestEvent>
    ) {
        fun result(result: Result<TestError, String>): Result<TestError, String> =
            resultMonitor {
                result
            }

        fun resultWithMonitorCall(result: Result<TestError, String>): Result<TestError, String> =
            resultMonitor {
                monitor.notify(StartEvent())
                result
            }
    }

    private lateinit var monitor: TestMonitor
    private lateinit var monitored: Monitored

    @Before
    fun setUp() {
        monitor = TestMonitor()
        monitored = Monitored(ResultMonitor(monitor) { toEvent() })
    }

    @Test
    fun `failure notifies monitor`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.result(Failure(AnError()))
        assertThat(monitor.events.size, equalTo(1))
        assertThat(monitor.events[0], instanceOf(ErrorEvent::class.java))
    }

    @Test
    fun `success doesn't notify monitor`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.result(Success("woo"))
        assertThat(monitor.events.size, equalTo(0))
    }

    @Test
    fun `calls to monitor work on failure`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.resultWithMonitorCall(Failure(AnError()))
        assertThat(monitor.events.size, equalTo(2))
        assertThat(monitor.events[0], instanceOf(StartEvent::class.java))
        assertThat(monitor.events[1], instanceOf(ErrorEvent::class.java))
    }

    @Test
    fun `calls to monitor work on success`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.resultWithMonitorCall(Success("woo"))
        assertThat(monitor.events.size, equalTo(1))
        assertThat(monitor.events[0], instanceOf(StartEvent::class.java))
    }

}