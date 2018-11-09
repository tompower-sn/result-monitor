package se.tp21.resultmonitor

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import se.tp21.*
import se.tp21.Result.Companion.failure
import se.tp21.Result.Companion.success

class ResultMonitorTest {

    private val monitor = Monitor<MonitoringEvent>()
    private val resultMonitor = ResultMonitor<Error, MonitoringEvent, Boolean>(monitor)

    private fun Boolean.monitored(): Result<Error, Boolean> =
        resultMonitor {
            if (this) {
                success(this)
            } else {
                failure(Error("failure message"))
            }
        }

    @Test
    fun `failure notifies monitor`() {
        assertThat(monitor.notifications, equalTo(0))
        false.monitored()
        assertThat(monitor.notifications, equalTo(1))
    }

    @Test
    fun `success doesn't notify monitor`() {
        assertThat(monitor.notifications, equalTo(0))
        true.monitored()
        assertThat(monitor.notifications, equalTo(0))
    }

    companion object {
        @Before
        fun `setup`() {

        }
    }

//    inline fun <reified T : Exception> extractFailure(failures: List<T>): List<String?> =
//        failures.map { failure ->
//            when (failure) {
//                is IOException -> failure.stackTrace.toString()
//                is OutOfMemoryError -> failure.message
//                else -> {
//                    failure.toString()
//                }
//            }
//        }

}