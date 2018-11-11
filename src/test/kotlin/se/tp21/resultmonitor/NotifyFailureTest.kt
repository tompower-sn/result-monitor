package se.tp21.resultmonitor

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import se.tp21.NotifyFailure
import se.tp21.Result
import se.tp21.Result.Failure
import se.tp21.Result.Success
import se.tp21.ResultMonitor
import se.tp21.resultmonitor.TestError.AnError
import se.tp21.resultmonitor.TestEvent.ErrorEvent
import se.tp21.resultmonitor.TestEvent.StartEvent

class NotifyFailureTest {

    private fun TestError.toEvent(): TestEvent =
        when (this) {
            is AnError -> ErrorEvent(message)
        }

    class Monitored(
        private val notifyFailure: NotifyFailure<TestError, TestEvent>
    ) {
        fun result(result: Result<TestError, String>): Result<TestError, String> =
            notifyFailure {
                result
            }

        fun resultWithMonitorCall(result: Result<TestError, String>): Result<TestError, String> =
            notifyFailure {
                monitor.notify(StartEvent())
                result
            }
    }

    private lateinit var monitor: TestMonitor
    private lateinit var monitored: Monitored

    @Before
    fun setUp() {
        monitor = TestMonitor()
        monitored = Monitored(ResultMonitor(monitor).notifyFailure { error -> error.toEvent() })
    }

    @Test
    fun `notifies monitor on failure`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.result(Failure(AnError()))
        assertThat(monitor.events.size, equalTo(1))
        assertThat(monitor.events[0], instanceOf(ErrorEvent::class.java))
    }

    @Test
    fun `doesn't notify monitor on success`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.result(Success("woo"))
        assertThat(monitor.events.size, equalTo(0))
    }

    @Test
    fun `allows direct calls to monitor on failure`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.resultWithMonitorCall(Failure(AnError()))
        assertThat(monitor.events.size, equalTo(2))
        assertThat(monitor.events[0], instanceOf(StartEvent::class.java))
        assertThat(monitor.events[1], instanceOf(ErrorEvent::class.java))
    }

    @Test
    fun `allows direct calls to monitor on success`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.resultWithMonitorCall(Success("woo"))
        assertThat(monitor.events.size, equalTo(1))
        assertThat(monitor.events[0], instanceOf(StartEvent::class.java))
    }

}