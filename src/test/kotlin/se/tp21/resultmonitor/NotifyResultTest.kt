package se.tp21.resultmonitor

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class NotifyResultTest {

    private fun Result<TestError, String>.toEvent(): TestEvent? =
        when (this) {
            is Failure -> when (error) {
                is AnError -> ErrorEvent()
            }
            is Success -> SuccessEvent(value)
        }

    private lateinit var monitor: TestMonitor
    private lateinit var notifyResult: NotifyResult<String, TestError, TestEvent>

    @Before
    fun setUp() {
        monitor = TestMonitor()
        notifyResult = monitor.notifyResult { it.toEvent() }
    }

    @Test
    fun `notifies monitor on failure`() {
        assertNoEvents()
        notifyResult {
            Failure(AnError())
        }
        assertTheseEvents(ErrorEvent())
    }

    @Test
    fun `notifies monitor on success`() {
        assertNoEvents()
        notifyResult {
            Success("woo")
        }
        assertTheseEvents(SuccessEvent("woo"))
    }

    @Test
    fun `allows direct calls to monitor on failure`() {
        assertNoEvents()
        notifyResult {
            monitor.notify(StartEvent())
            Failure(AnError())
        }
        assertTheseEvents(StartEvent(), ErrorEvent())
    }

    @Test
    fun `allows direct calls to monitor on success`() {
        assertNoEvents()
        notifyResult {
            monitor.notify(StartEvent())
            Success("woo")
        }
        assertTheseEvents(StartEvent(), SuccessEvent("woo"))
    }

    private fun assertTheseEvents(vararg events: TestEvent) {
        assertThat(monitor.events.toList(), equalTo(events.toList()))
    }

    private fun assertNoEvents() {
        assertTheseEvents()
    }

}