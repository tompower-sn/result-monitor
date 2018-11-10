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
import se.tp21.resultmonitor.ResultMonitorTest.TestEvent.AnEvent

class ResultMonitorTest {

    sealed class TestError(override val message: String) : Error {
        class AnError : TestError("error")
    }

    sealed class TestEvent(override val message: String) : Event {
        class AnEvent : TestEvent("event")
    }

    private fun TestError.toEvent(): TestEvent =
        when (this) {
            is AnError -> AnEvent()
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
        fun result(boolean: Boolean): Result<Error, String> =
            resultMonitor {
                if (boolean) {
                    Success("success")
                } else {
                    Failure(AnError())
                }
            }
    }

    private lateinit var monitor: TestMonitor
    private lateinit var monitored: Monitored

    @Before
    fun setUp() {
        monitor = TestMonitor()
        monitored = Monitored(ResultMonitor(monitor) { error: TestError -> error.toEvent() })
    }

    @Test
    fun `failure notifies monitor`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.result(false)
        assertThat(monitor.events.size, equalTo(1))
        assertThat(monitor.events[0], instanceOf(AnEvent::class.java))
    }

    @Test
    fun `success doesn't notify monitor`() {
        assertThat(monitor.events.size, equalTo(0))
        monitored.result(true)
        assertThat(monitor.events.size, equalTo(0))
    }

}