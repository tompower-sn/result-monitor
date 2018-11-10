package se.tp21

interface Monitor<Ev: Event> {
    fun notify(event: Ev)
}

interface Event {
    val message: String
}

