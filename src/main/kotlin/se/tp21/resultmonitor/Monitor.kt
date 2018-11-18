package se.tp21.resultmonitor

interface Monitor<Ev: Event> {
    fun notify(event: Ev)
}

interface Event

class DefaultEvent : Event

