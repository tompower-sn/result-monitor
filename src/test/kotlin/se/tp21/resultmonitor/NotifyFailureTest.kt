package se.tp21.resultmonitor

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import se.tp21.NotifyFailure
import se.tp21.Result.Failure
import se.tp21.Result.Success
import se.tp21.notifyFailure

class NotifyFailureTest {

    private fun TestError.toEvent(): TestEvent =
        when (this) {
            is AnError -> ErrorEvent()
        }

    private lateinit var monitor: TestMonitor
    private lateinit var notifyFailure: NotifyFailure<TestError, TestEvent>

    @Before
    fun setUp() {
        monitor = TestMonitor()
        notifyFailure = monitor.notifyFailure { error -> error.toEvent() }
    }

    @Test
    fun `notifies monitor on failure`() {
        assertNoEvents()
        notifyFailure {
            Failure(AnError())
        }
        assertTheseEvents(ErrorEvent())
    }

    @Test
    fun `doesn't notify monitor on success`() {
        assertNoEvents()
        notifyFailure {
            Success("woo")
        }
        assertNoEvents()
    }

    @Test
    fun `allows direct calls to monitor on failure`() {
        assertNoEvents()
        notifyFailure {
            monitor.notify(StartEvent())
            Failure(AnError())
        }
        assertTheseEvents(StartEvent(), ErrorEvent())
    }

    @Test
    fun `allows direct calls to monitor on success`() {
        assertNoEvents()
        notifyFailure {
            monitor.notify(StartEvent())
            Success("woo")
        }
        assertTheseEvents(StartEvent())
    }

    private fun assertTheseEvents(vararg events: TestEvent) {
        assertThat(monitor.events.toList(), equalTo(events.toList()))
    }

    private fun assertNoEvents() {
        assertTheseEvents()
    }

}