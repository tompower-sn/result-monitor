package se.tp21

class Monitor<T: MonitoringEvent> {
    var notifications: Int = 0

    fun notify(event: T) {
        notifications +=1
    }
}

