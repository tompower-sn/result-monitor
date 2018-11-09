package se.tp21

sealed class Result<out ERR, out T> {
    data class Failure<out ERR : Error>(val value: ERR) : Result<ERR, Nothing>()
    data class Success<out T>(val value: T) : Result<Nothing, T>()

    companion object {
        fun <ERR : Error> failure(err: ERR): Result<ERR, Nothing> = Failure(err)

        fun <T> success(value: T): Result<Nothing, T> = Success(value)
    }
}

