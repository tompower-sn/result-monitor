package se.tp21

interface Monitor<T: Event> {
    fun notify(event: T)
}

interface Event {
    val message: String
}

