package se.tp21.resultmonitor

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class NotifySuccessTest {

    private fun Success<String>.toEvent(): TestEvent? = SuccessEvent(value)

    private lateinit var monitor: TestMonitor
    private lateinit var notifySuccess: NotifyResult<String, TestError, TestEvent>

    @Before
    fun setUp() {
        monitor = TestMonitor()
        notifySuccess = monitor.notifySuccess { it.toEvent() }
    }

    @Test
    fun `notifies monitor on success`() {
        assertNoEvents()
        notifySuccess {
            Success("woo")
        }
        assertTheseEvents(SuccessEvent("woo"))
    }

    @Test
    fun `doesn't notify monitor on failure`() {
        assertNoEvents()
        notifySuccess {
            Failure(AnError())
        }
        assertNoEvents()
    }

    @Test
    fun `allows direct calls to monitor on success`() {
        assertNoEvents()
        notifySuccess {
            monitor.notify(StartEvent())
            Success("woo")
        }
        assertTheseEvents(StartEvent(), SuccessEvent("woo"))
    }

    @Test
    fun `allows direct calls to monitor on faiure`() {
        assertNoEvents()
        notifySuccess {
            monitor.notify(StartEvent())
            Failure(AnError())
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