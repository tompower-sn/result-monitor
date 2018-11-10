package se.tp21

sealed class Result<out ERR, out T> {
    data class Failure<out ERR : Error>(val error: ERR) : Result<ERR, Nothing>()
    data class Success<out T>(val value: T) : Result<Nothing, T>()
}

interface Error{
    val message: String
}

